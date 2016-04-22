package org.codeontology;

/**
 * Provide basic interfaces with default ontologies.
 */

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * General programming language ontology interface.
 */
public class Ontology {

    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String WOC = "http://rdf.webofcode.org/woc/";

    private static Model ontology() {
        try {
            File ontology = new File(System.getProperty("user.dir") + "/ontology/woc.xml");
            FileReader reader = new FileReader(ontology);
            return ModelFactory.createDefaultModel().read(reader, "");
        } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
        }
    }

    public static Model getModel() {
        return ModelFactory.createDefaultModel();
    }

    public static Model model = ontology();

    public static final Resource PACKAGE_CLASS = model.getResource(WOC + "Package");

    public static final Resource CLASS_CLASS = model.getResource(WOC + "Class");

    public static final Resource INTERFACE_CLASS = model.getResource(WOC + "Interface");

    public static final Resource ENUM_CLASS = model.getResource(WOC + "Enum");

    public static final Resource ANNOTATION_CLASS = model.getResource(WOC + "Annotation");

    public static final Resource PRIMITIVE_CLASS = model.getResource(WOC + "Primitive");

    public static final Resource ARRAY_CLASS = model.getResource(WOC + "Array");

    public static final Resource TYPE_VARIABLE_CLASS =  model.getResource(WOC + "TypeVariable");

    public static final Resource PARAMETERIZED_TYPE_CLASS =  model.getResource(WOC + "ParameterizedType");

    public static final Resource FIELD_CLASS =  model.getResource(WOC + "Field");

    public static final Resource CONSTRUCTOR_CLASS = model.getResource(WOC + "Constructor");

    public static final Resource METHOD_CLASS = model.getResource(WOC + "Method");

    public static final Resource PARAMETER_CLASS = model.getResource(WOC + "Parameter");

    public static final Resource VARIABLE_CLASS = model.getResource(WOC + "Variable");

    public static final Resource LAMBDA_CLASS = model.getResource(WOC + "Lambda");

    public static final Resource TYPE_ARGUMENT_CLASS = model.getResource(WOC + "TypeArgument");


    public static final Property RDF_TYPE_PROPERTY = model.getProperty(RDF + "type");

    public static final Property JAVA_TYPE_PROPERTY = model.getProperty(WOC + "type");

    public static final Property COMMENT_PROPERTY = model.getProperty(RDFS + "comment");

    public static final Property NAME_PROPERTY = model.getProperty(WOC + "name");

    public static final Property DECLARED_BY_PROPERTY = model.getProperty(WOC + "declaredBy");

    public static final Property RETURNS_PROPERTY = model.getProperty(WOC + "returns");

    public static final Property RETURNS_VAR_PROPERTY = model.getProperty(WOC + "returnsVar");

    public static final Property RETURNS_FIELD_PROPERTY = model.getProperty(WOC + "returnsField");

    public static final Property CONSTRUCTS_PROPERTY = model.getProperty(WOC + "constructs");

    public static final Property PARAMETER_PROPERTY = model.getProperty(WOC + "parameter");

    public static final Property POSITION_PROPERTY = model.getProperty(WOC + "position");

    public static final Property SOURCE_CODE_PROPERTY = model.getProperty(WOC + "sourceCode");

    public static final Property THROWS_PROPERTY = model.getProperty(WOC + "throws");

    public static final Property IS_NESTED_IN_PROPERTY = model.getProperty(WOC + "nestedIn");

    public static final Property MODIFIER_PROPERTY = model.getProperty(WOC + "modifier");

    public static final Property VISIBILITY_PROPERTY = model.getProperty(WOC + "visibility");

    public static final Property REQUESTS_PROPERTY = model.getProperty(WOC + "requests");

    public static final Property EXTENDS_PROPERTY = model.getProperty(WOC + "extends");

    public static final Property IMPLEMENTS_PROPERTY = model.getProperty(WOC + "implements");

    public static final Property SUPER_PROPERTY = model.getProperty(WOC + "super");

    public static final Property PACKAGE_OF_PROPERTY = model.getProperty(WOC + "packageOf");

    public static final Property ARRAY_OF_PROPERTY = model.getProperty(WOC + "arrayOf");

    public static final Property DIMENSIONS_PROPERTY = model.getProperty(WOC + "dimensions");

    public static final Property FORMAL_TYPE_PARAMETER_PROPERTY = model.getProperty(WOC + "formalTypeParameter");

    public static final Property ACTUAL_TYPE_ARGUMENT_PROPERTY = model.getProperty(WOC + "actualTypeArgument");

    public static final Property RAW_TYPE_PROPERTY = model.getProperty(WOC + "rawType");

    public static final Property ANNOTATION_PROPERTY = model.getProperty(WOC + "annotation");

    public static final Property OVERRIDES_PROPERTY = model.getProperty(WOC + "overrides");


    public static final Resource PUBLIC_INDIVIDUAL = model.getResource(WOC + "Public");

    public static final Resource PRIVATE_INDIVIDUAL = model.getResource(WOC + "Private");

    public static final Resource PROTECTED_INDIVIDUAL = model.getResource(WOC + "Protected");

    public static final Resource DEFAULT_INDIVIDUAL = model.getResource(WOC + "Default");

    public static final Resource ABSTRACT_INDIVIDUAL = model.getResource(WOC + "Abstract");

    public static final Resource FINAL_INDIVIDUAL = model.getResource(WOC + "Final");

    public static final Resource STATIC_INDIVIDUAL = model.getResource(WOC + "Static");

    public static final Resource SYNCHRONIZED_INDIVIDUAL = model.getResource(WOC + "Synchronized");

    public static final Resource VOLATILE_INDIVIDUAL = model.getResource(WOC + "Volatile");

}

