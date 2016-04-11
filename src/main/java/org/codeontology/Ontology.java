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
    public static final String BASE_URI = "http://rdf.webofcode.org/woc/";

    /**
     * Create a default model with programming languages getModel.
     * @return	A default model with programming languages getModel.
     */
    public static Model getModel() {
        try {
            File ontology = new File(System.getProperty("user.dir") + "/ontology/woc.xml");
            FileReader reader = new FileReader(ontology);
            return ModelFactory.createDefaultModel().read(reader, "");
        } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
        }
    }

    public final static Model model = getModel();

    public static final Resource PACKAGE_CLASS = model.getResource(BASE_URI + "Package");

    public static final Resource CLASS_CLASS = model.getResource(BASE_URI + "Class");

    public static final Resource INTERFACE_CLASS = model.getResource(BASE_URI + "Interface");

    public static final Resource ENUM_CLASS = model.getResource(BASE_URI + "Enum");

    public static final Resource ANNOTATION_CLASS = model.getResource(BASE_URI + "Annotation");

    public static final Resource PRIMITIVE_CLASS = model.getResource(BASE_URI + "Primitive");

    public static final Resource GENERIC_CLASS =  model.getResource(BASE_URI + "Generic");

    public static final Resource FIELD_CLASS =  model.getResource(BASE_URI + "Field");

    public static final Resource CONSTRUCTOR_CLASS = model.getResource(BASE_URI + "Constructor");

    public static final Resource METHOD_CLASS = model.getResource(BASE_URI + "Method");

    public static final Resource PARAMETER_CLASS = model.getResource(BASE_URI + "Parameter");

    public static final Resource VARIABLE_CLASS = model.getResource(BASE_URI + "Variable");

    public static final Resource LAMBDA_CLASS = model.getResource(BASE_URI + "Lambda");


    public static final Property RDF_TYPE_PROPERTY = model.getProperty(RDF + "type");

    public static final Property JAVA_TYPE_PROPERTY = model.getProperty(BASE_URI + "type");

    public static final Property COMMENT_PROPERTY = model.getProperty(RDFS + "comment");

    public static final Property NAME_PROPERTY = model.getProperty(BASE_URI + "name");

    public static final Property DECLARED_BY_PROPERTY = model.getProperty(BASE_URI + "declaredBy");

    public static final Property RETURNS_PROPERTY = model.getProperty(BASE_URI + "returns");

    public static final Property RETURNS_VAR_PROPERTY = model.getProperty(BASE_URI + "returnsVar");

    public static final Property RETURNS_FIELD_PROPERTY = model.getProperty(BASE_URI + "returnsField");

    public static final Property CONSTRUCTS_PROPERTY = model.getProperty(BASE_URI + "constructs");

    public static final Property PARAMETER_PROPERTY = model.getProperty(BASE_URI + "parameter");

    public static final Property POSITION_PROPERTY = model.getProperty(BASE_URI + "position");

    public static final Property SOURCE_CODE_PROPERTY = model.getProperty(BASE_URI + "sourceCode");

    public static final Property THROWS_PROPERTY = model.getProperty(BASE_URI + "throws");

    public static final Property CONTAINS_PROPERTY = model.getProperty(BASE_URI + "contains");

    public static final Property MODIFIER_PROPERTY = model.getProperty(BASE_URI + "modifier");

    public static final Property VISIBILITY_PROPERTY = model.getProperty(BASE_URI + "visibility");

    public static final Property REQUESTS_PROPERTY = model.getProperty(BASE_URI + "requests");

    public static final Property EXTENDS_PROPERTY = model.getProperty(BASE_URI + "extends");

    public static final Property IMPLEMENTS_PROPERTY = model.getProperty(BASE_URI + "implements");

    public static final Property PACKAGE_OF_PROPERTY = model.getProperty(BASE_URI + "packageOf");


    public static final Resource PUBLIC_RESOURCE = model.getResource(BASE_URI + "Public");

    public static final Resource PRIVATE_RESOURCE = model.getResource(BASE_URI + "Private");

    public static final Resource PROTECTED_RESOURCE = model.getResource(BASE_URI + "Protected");

    public static final Resource DEFAULT_RESOURCE = model.getResource(BASE_URI + "Default");

    public static final Resource ABSTRACT_RESOURCE = model.getResource(BASE_URI + "Abstract");

    public static final Resource FINAL_RESOURCE = model.getResource(BASE_URI + "Final");

    public static final Resource STATIC_RESOURCE = model.getResource(BASE_URI + "Static");

    public static final Resource SYNCHRONIZED_RESOURCE = model.getResource(BASE_URI + "Synchronized");

    public static final Resource VOLATILE_RESOURCE = model.getResource(BASE_URI + "Volatile");

}

