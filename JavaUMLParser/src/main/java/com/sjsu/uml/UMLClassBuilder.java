/**
 * 
 */
package com.sjsu.uml;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.sjsu.parser.AssociationType;

/**
 * @author Meenakshi
 *
 */
public class UMLClassBuilder {
	// The class/interface for which UML class is being built
	private ClassOrInterfaceDeclaration classOrInterface = null;
	// List of all classes/interfaces
	private List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations = null; 
	private UMLClass umlClass = new UMLClass();

	public UMLClassBuilder(ClassOrInterfaceDeclaration classOrInterface, List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations) {
		this.classOrInterface = classOrInterface;
		this.classOrInterfaceDeclarations = classOrInterfaceDeclarations;
	}

	/**
	 * Build UML object from ClassOrInterfaceDeclaration
	 * 
	 */
	public UMLClass buildUMLClass(){
		setTypeDetails();
		setConstructName();
		setConstructors();
		setExtendedTypes();
		setImplementedTypes();
		setFields();
		setMethods();
		setUMLAssociations();
		setUMLRelations();
		return umlClass;
	}


	/**
	 * Specify if class/interface
	 * 
	 */
	private void setTypeDetails(){
		if(classOrInterface.isInterface())
			umlClass.setIsInterface(classOrInterface.isInterface());
	}

	private void setConstructName(){
		umlClass.setClassName(classOrInterface.getNameAsString());
	}

	@SuppressWarnings("rawtypes")
	private void setConstructors(){
		List<BodyDeclaration> bodyDeclarations = classOrInterface.getMembers().stream().filter(member -> member instanceof ConstructorDeclaration).collect(Collectors.toList());
		for(BodyDeclaration bodyDeclaration : bodyDeclarations) {
			ConstructorDeclaration constructorDeclaration = ((ConstructorDeclaration)bodyDeclaration);
			if(constructorDeclaration != null){
				umlClass.addConstructors(constructorDeclaration);
			}
		}
	}

	private void setExtendedTypes() {
		NodeList<ClassOrInterfaceType> extendedTypes = classOrInterface.getExtendedTypes();
		if(!extendedTypes.isEmpty()){
			umlClass.setExtendedTypes(StringUtils.join(extendedTypes.iterator(), ','));
		}

	}

	private void setImplementedTypes() {
		NodeList<ClassOrInterfaceType> implementedTypes = classOrInterface.getExtendedTypes();
		if(!implementedTypes.isEmpty()){
			umlClass.setImplementedTypes(StringUtils.join(implementedTypes.iterator(), ','));
		}
	}

	private void setFields() {
		List<FieldDeclaration> fields = classOrInterface.getFields();
		umlClass.setFields(fields);
	}

	private void setMethods() {
		List<MethodDeclaration> methods = classOrInterface.getMethods();
		umlClass.setMethods(methods);
	}

	private void setUMLAssociations() {
		//TODO - check for types of field - List , arraylist, map collection etc
		List<FieldDeclaration> fields = classOrInterface.getFields();
		String containingClass = classOrInterface.getNameAsString();
		for(FieldDeclaration field : fields) {
			String containedClass = field.getVariable(0).getType().toString();
			if(getClassOrInterfaceByName(containedClass) != null || containedClass.startsWith("Collection")){
				if(containedClass.startsWith("Collection")){
					containedClass = StringUtils.substringBetween(containedClass, "<", ">");
					umlClass.addUmlAssociations(new String[] {containingClass, containedClass, AssociationType.ONE_TO_MANY.toString()});

				}else {
					umlClass.addUmlAssociations(new String[] {containingClass, containedClass, AssociationType.ONE_TO_ONE.toString()});
				}
			}
		}

	}

	/**
	 * Get the class or interface declaration by name
	 * 
	 * @param classOrInterfaceName
	 * @return
	 */
	public ClassOrInterfaceDeclaration getClassOrInterfaceByName(String classOrInterfaceName){
		for(ClassOrInterfaceDeclaration classOrInterfaceDeclaration: classOrInterfaceDeclarations){
			if(classOrInterfaceDeclaration.getNameAsString().equalsIgnoreCase(classOrInterfaceName))
				return classOrInterfaceDeclaration;
		}
		return null;

	}

	private void setUMLRelations() {
		getMethodDependencies();

	}

	private void getMethodDependencies() {
		for(MethodDeclaration method : umlClass.getMethods()){
			NodeList<Parameter> parameters = method.getParameters();
			parameters.stream().forEach(parameter -> 
			{	
				String parameterClass = parameter.getType().toString();
				ClassOrInterfaceDeclaration classOrInterfaceDeclaration = getClassOrInterfaceByName(parameterClass);
				if(classOrInterfaceDeclaration != null && classOrInterfaceDeclaration.isInterface()){
					umlClass.addUmlRelations(new String[]{classOrInterface.getNameAsString(), parameter.getType().toString(), RelationshipType.USES.toString()});
				}
			});
		}
		
	}


}
