package com.berniecode.mixin4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.janino.ClassBodyEvaluator;

public class ClassGeneratingMixer implements Mixer {

	@Override
	public <T> Factory<? extends T> getFactory(Class<T> mixinBase, Mixin<T> mixin) {
		List<Mixin<T>> mixins = new ArrayList<Mixin<T>>();
		mixins.add(mixin);
		return getFactory(mixinBase, mixins);
	}

	@Override
	public <T> Factory<? extends T> getFactory(Class<T> mixinBase, List<Mixin<T>> mixins) {
		return new JanioSourceGeneratingFactory<T>(mixinBase, mixins);
	}

}

class JanioSourceGeneratingFactory<T> implements Factory<T> {
	
	private static final String SAVE_ROOT_PROPERTY = "com.berniecode.mixin4j.generatedSourceFolder";

	// a text string that comments out lines in the compiled code but exposes it in the saved files
	private static final String COMPILATION_ONLY = "//stripme//";

	private final Class<? extends T> mixedClass;

	public JanioSourceGeneratingFactory(Class<T> mixinBase, List<Mixin<T>> mixins) {
		mixedClass = getMixedClass(mixinBase, mixins);
	}

	private Class<? extends T> getMixedClass(Class<T> mixinBase, List<Mixin<T>> mixins) {
		JavaSourceBuilder jsb = new JavaSourceBuilder();
		
		String mixedClassName = mixinBase.getSimpleName() + "WithMixins";
		jsb.line(COMPILATION_ONLY + "class %s extends %s {", mixedClassName, mixinBase.getName());
		jsb.line();
		
		int mixinId=0;
		for (Mixin<T> mixin: mixins) {
			mixinId++;
			// mixin variable declaration
			String implClassName = mixin.getMixinImpl().getName();
			String varName = mixin.getMixinType().getSimpleName() + mixinId;
			varName = Character.toLowerCase(varName.charAt(0)) + varName.substring(1);
			jsb.line("// delegation of methods in mixin type %s", mixin.getMixinType());
			jsb.line("// to implementation %s", mixin.getMixinImpl());
			jsb.line("private %s %s = new %1$s();", implClassName, varName);
			// MixinAware processing
			if (MixinAware.class.isAssignableFrom(mixin.getMixinImpl())) {
				jsb.line("{");
				jsb.line("%s.setMixinBase(this); // MixinAware fulfilment", varName);
				jsb.line("}");
			}
			// method delegations
			for (Method method: mixin.getMixinType().getMethods()) {
				String returnType = method.getReturnType().getName();
				String methodName = method.getName();
				jsb.line("public %s %s(%s) {", returnType, methodName , getArgs(method.getParameterTypes(), true));
				String rtn = method.getReturnType().equals(Void.TYPE) ? "" : "return ";
				jsb.line("%s%s.%s(%s);", rtn, varName, methodName, getArgs(method.getParameterTypes(), false));
				jsb.line("}");
			}
		}
		
		// write constructors
		for (Constructor<?> constructor: mixinBase.getDeclaredConstructors()) {
			if (!Modifier.isPrivate(constructor.getModifiers())) {
				jsb.line("public %s(%s) {", mixedClassName, getArgs(constructor.getParameterTypes(), true));
				jsb.line("super(%s);", getArgs(constructor.getParameterTypes(), false));
				jsb.line("}");
			}
		}
		
		// close class
		jsb.line(COMPILATION_ONLY + "}");
		
		ClassBodyEvaluator eval = new ClassBodyEvaluator();
		
		String fullMixedClassName = mixinBase.getPackage().getName() + "." + mixedClassName;
		eval.setExtendedType(mixinBase);
		eval.setClassName(fullMixedClassName);
		eval.setParentClassLoader(mixinBase.getClassLoader());
		String sourceCode = jsb.toString();
		try {
			eval.cook(sourceCode);
		} catch (Exception e) {
			throw new MixinException("Error while generating source code: \n" + sourceCode, e);
		}
		
		Class<? extends T> mixedClass = eval.getClazz();
		
		String sourceRoot = System.getProperty(SAVE_ROOT_PROPERTY);
		if (sourceRoot != null) {
			saveSourceCode(sourceRoot, sourceCode, mixedClass);
		}
		
		return mixedClass;
	}

	private void saveSourceCode(String sourceRoot, String sourceCode, Class<?> mixedClass) {
		String packagePath = mixedClass.getPackage().getName().replace('.', '/');
		File packageFolder = new File(sourceRoot, packagePath);
		if (!packageFolder.exists() && !packageFolder.mkdirs()) {
			throw new MixinException("Could not create folder " + packageFolder + ", fix the issue or remove the " + SAVE_ROOT_PROPERTY + " property.");
		}
		File javaFile = new File(packageFolder, mixedClass.getSimpleName() + ".java");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(javaFile));
			bw.write(sourceCode.replace(COMPILATION_ONLY, ""));
			bw.close();
		} catch (IOException e) {
			throw new MixinException("Could not write file " + javaFile + ", fix the issue or remove the " + SAVE_ROOT_PROPERTY + " property.");
		}
	}

	private String getArgs(Class<?>[] parameterTypes, boolean withTypes) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<parameterTypes.length; i++) {
			if (withTypes) {
				sb.append(parameterTypes[i].getName());
				sb.append(' ');
			}
			sb.append("arg");
			sb.append(i);
			if (i < parameterTypes.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	@Override
	public T newInstance() {
		try {
			return mixedClass.newInstance();
		} catch (Exception e) {
			throw new MixinException("The constructor invocation threw an exception", e);
		}
	}

	@Override
	public T newInstance(Class<?>[] constructorArgTypes,
			Object[] constructorArgs) {
		if (constructorArgTypes.length == 0) {
			return newInstance();
		}
		try {
			return mixedClass.getConstructor(constructorArgTypes).newInstance(constructorArgs);
		} catch (Exception e) {
			throw new MixinException("The constructor invocation threw an exception", e);
		}
	}
	
}

class JavaSourceBuilder {
	
	private int indentation = 0;
	
	private StringBuilder sb = new StringBuilder();
	

	public void line(String text, Object... parameters) {
		if (text.indexOf('}') != -1) {
			indentation --;
		}
		for (int i=0; i<indentation; i++) {
			sb.append("    ");
		}
		sb.append(String.format(text, (Object[]) parameters));
		sb.append('\n');
		if (text.indexOf('{') != -1) {
			indentation ++;
		}
	}
	
	public void line() {
		sb.append('\n');
	}

	public String toString() {
		return sb.toString();
	}
	
}


/**
* @author JavaMixin. See what I did there?
*/
//class RandomNumberSequence$Mixin extends com.berniecode.mixin4j.test.aware.RandomNumberSequence {
//
//    // delegation of methods in mixin type interface com.berniecode.mixin4j.test.aware.DumpToXmlMixin
//    // to implementation class com.berniecode.mixin4j.test.aware.PrintIteratorMixinImpl
//    private com.berniecode.mixin4j.test.aware.DumpIteratorToXml dumpToXmlMixin1 = new com.berniecode.mixin4j.test.aware.DumpIteratorToXml();
//    {
//        dumpToXmlMixin1.setMixinBase(this); // MixinAware fulfilment
//    }
//    public java.lang.String getXml() {
//        return dumpToXmlMixin1.getXml();
//    }
//    public RandomNumberSequence$Mixin(int arg0, int arg1, int arg2) {
//        super(arg0, arg1, arg2);
//    }
//}
