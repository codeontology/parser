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

    private static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    private final static String baseURI = "http://rdf.webofcode.org/woc/";

    public final static Model model = ontology();

    public static Model baseModel() {
        return ModelFactory.createDefaultModel();
    }

    /**
     * Create a default model with programming languages ontology.
     * @return	A default model with programming languages ontology.
     */
    public static Model ontology() {
        try {
            File ontology = new File(System.getProperty("user.dir") + "/ontology/woc.xml");
            FileReader reader = new FileReader(ontology);
            return ModelFactory.createDefaultModel().read(reader, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    /**
     * Get a new base ontology.
     * @return	A new ontology.
     */
    public static Model model() {
        return ontology();
    }

    public static Property getTypeProperty() {
        return model.getProperty(rdf + "type");
    }

    public static Property getAbstractTypeProperty() {
        return model.getProperty(baseURI + "type");
    }

    public static Property getCommentProperty() {
        return model.getProperty(rdfs + "comment");
    }

    public static Property getNameProperty() {
        return model.getProperty(baseURI + "name");
    }

    public static Property getDeclaredByProperty() {
        return model.getProperty(baseURI + "declared_by");
    }

    public static Property getReturnProperty() {
        return model.getProperty(baseURI + "returns");
    }

    public static Property getReturnLocalFieldProperty() {
        return model.getProperty(baseURI + "returns_var");
    }

    public static Property getReturnClassFieldProperty() {
        return model.getProperty(baseURI + "returns_field");
    }

    public static Property getConstructsProperty() {
        return model.getProperty(baseURI + "constructs");
    }

    public static Property getParameterProperty() {
        return model.getProperty(baseURI + "parameter");
    }

    public static Property getParameterPositionProperty() {
        return model.getProperty(baseURI + "parameter_position");
    }

    public static Resource getParameterIndividual() {
        return model.getResource(baseURI + "Parameter");
    }

    // default value
    /*public static Resource getExpressionIndividual() {
        return  model.getResource(baseURI + "Expression");
    }*/

    public static Property getSourceCodeProperty () {
        return model.getProperty(baseURI + "source_code");
    }

    /*
    public static Resource getUnknownType() {
        return model.getResource(baseURI + "Unknown");
    }*/
    public static Resource getPrimitiveIndividual() {
        return model.getResource(baseURI + "Primitive");
    }

    public static Resource getClassIndividual() {
        return model.getResource(baseURI + "Class");
    }

    public static Resource getInterfaceIndividual() {
        return model.getResource(baseURI + "Interface");
    }

    public static Resource getEnumIndividual () {
        return model.getProperty(baseURI + "Enum");
    }

    public static Resource getMethodIndividual() {
        return model.getResource(baseURI + "Method");
    }

    public static Resource getConstructorIndividual() {
        return model.getResource(baseURI + "Constructor");
    }

    public static Property getPackageIndividual() {
        return model.getProperty(baseURI + "Package");
    }

    public static Property getAnnotationIndividual() {
        return model.getProperty(baseURI + "Annotation");
    }

    public static Property getExtendsProperty() {
        return model.getProperty(baseURI + "extends");
    }

    public static Property getImplementsProperty() {
        return model.getProperty(baseURI + "implements");
    }

    public static Property getGenericProperty() {
        return model.getProperty(baseURI + "generic");
    }

    public static Property getGenericPositionProperty() {
        return model.getProperty(baseURI + "generic_position");
    }
/*
    public static Property getInvokesProperty() {
        return model.getProperty(baseURI + "Invokes");
    }*/

    public static Property getPackageProperty() {
        return model.getProperty(baseURI + "package");
    }

    public static Property getPublicProperty() {
        return model.getProperty(baseURI + "Public");
    }

    public static Property getPrivateProperty() {
        return model.getProperty(baseURI + "Private");
    }

    public static Property getProtectedProperty() {
        return model.getProperty(baseURI + "Protected");
    }

    public static Property getDefaultProperty() {
        return model.getProperty(baseURI + "Default");
    }

    public static Property getAbstractProperty() {
        return model.getProperty(baseURI + "Abstract");
    }

    public static Property getFinalProperty() {
        return model.getProperty(baseURI + "Final");
    }

    public static Property getStaticProperty() {
        return model.getProperty(baseURI + "Static");
    }

    public static Property getSynchronizedProperty() {
        return model.getProperty(baseURI + "Synchronized");
    }

    public static Property getVolatileProperty() {
        return model.getProperty(baseURI + "Volatile");
    }

    public static Property getModifierProperty() {
        return model.getProperty(baseURI + "has_modifier");
    }

    public static Property getEncapsulationProperty() {
        return model.getProperty(baseURI + "encapsulation");
    }

    public static Property getRequestsProperty() {
        return model.getProperty(baseURI + "requests");
    }

    public static Resource getFieldClass() {
        return  model.getProperty(baseURI + "Field");
    }

    public static Resource getLocalVariableClass() {
        return model.getProperty(baseURI + "Variable");
    }

    public static Property getThrowsProperty() {
        return model.getProperty(baseURI + "throws");
    }

    /*public static Resource getAnnotationTypeIndividual () {
        return model.getProperty(baseURI + "AnnotationType");
    }*/

    public static Resource getLambdaIndividual () {
        return model.getResource(baseURI + "Lambda");
    }

    public static Property getLambdaImplementsProperty () {
        return model.getProperty(baseURI + "lambda_implements");
    }

    public static String getBaseURI() {
        return baseURI;
    }
}

