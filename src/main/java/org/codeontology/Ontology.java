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

    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private final static String BASE_URI = "http://rdf.webofcode.org/woc/";

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
        return model.getProperty(RDF + "type");
    }

    public static Property getAbstractTypeProperty() {
        return model.getProperty(BASE_URI + "type");
    }

    public static Property getCommentProperty() {
        return model.getProperty(RDFS + "comment");
    }

    public static Property getNameProperty() {
        return model.getProperty(BASE_URI + "name");
    }

    public static Property getDeclaredByProperty() {
        return model.getProperty(BASE_URI + "declaredBy");
    }

    public static Property getReturnProperty() {
        return model.getProperty(BASE_URI + "returns");
    }

    public static Property getReturnLocalFieldProperty() {
        return model.getProperty(BASE_URI + "returnsVar");
    }

    public static Property getReturnClassFieldProperty() {
        return model.getProperty(BASE_URI + "returnsField");
    }

    public static Property getConstructsProperty() {
        return model.getProperty(BASE_URI + "constructs");
    }

    public static Property getParameterProperty() {
        return model.getProperty(BASE_URI + "parameter");
    }

    public static Property getParameterPositionProperty() {
        return model.getProperty(BASE_URI + "position");
    }

    public static Resource getParameterIndividual() {
        return model.getResource(BASE_URI + "Parameter");
    }

    // default value
    /*public static Resource getExpressionIndividual() {
        return  model.getResource(BASE_URI + "Expression");
    }*/

    public static Property getSourceCodeProperty () {
        return model.getProperty(BASE_URI + "sourceCode");
    }

    /*
    public static Resource getUnknownType() {
        return model.getResource(BASE_URI + "Unknown");
    }*/
    public static Resource getPrimitiveIndividual() {
        return model.getResource(BASE_URI + "Primitive");
    }

    public static Resource getClassIndividual() {
        return model.getResource(BASE_URI + "Class");
    }

    public static Resource getInterfaceIndividual() {
        return model.getResource(BASE_URI + "Interface");
    }

    public static Resource getEnumIndividual () {
        return model.getProperty(BASE_URI + "Enum");
    }

    public static Resource getMethodIndividual() {
        return model.getResource(BASE_URI + "Method");
    }

    public static Resource getConstructorIndividual() {
        return model.getResource(BASE_URI + "Constructor");
    }

    public static Property getPackageIndividual() {
        return model.getProperty(BASE_URI + "Package");
    }

    public static Property getAnnotationIndividual() {
        return model.getProperty(BASE_URI + "Annotation");
    }

    public static Property getExtendsProperty() {
        return model.getProperty(BASE_URI + "extends");
    }

    public static Property getImplementsProperty() {
        return model.getProperty(BASE_URI + "implements");
    }
/*
    public static Property getGenericProperty() {
        return model.getProperty(BASE_URI + "generic");
    }

    public static Property getGenericPositionProperty() {
        return model.getProperty(BASE_URI + "generic_position");
    }

    public static Property getInvokesProperty() {
        return model.getProperty(BASE_URI + "Invokes");
    }*/

    public static Property getPackageProperty() {
        return model.getProperty(BASE_URI + "packageOf");
    }

    public static Property getPublicProperty() {
        return model.getProperty(BASE_URI + "Public");
    }

    public static Property getPrivateProperty() {
        return model.getProperty(BASE_URI + "Private");
    }

    public static Property getProtectedProperty() {
        return model.getProperty(BASE_URI + "Protected");
    }

    public static Property getDefaultProperty() {
        return model.getProperty(BASE_URI + "Default");
    }

    public static Property getAbstractProperty() {
        return model.getProperty(BASE_URI + "Abstract");
    }

    public static Property getFinalProperty() {
        return model.getProperty(BASE_URI + "Final");
    }

    public static Property getStaticProperty() {
        return model.getProperty(BASE_URI + "Static");
    }

    public static Property getSynchronizedProperty() {
        return model.getProperty(BASE_URI + "Synchronized");
    }

    public static Property getVolatileProperty() {
        return model.getProperty(BASE_URI + "Volatile");
    }

    public static Property getModifierProperty() {
        return model.getProperty(BASE_URI + "modifier");
    }

    public static Property getEncapsulationProperty() {
        return model.getProperty(BASE_URI + "encapsulation");
    }

    public static Property getRequestsProperty() {
        return model.getProperty(BASE_URI + "requests");
    }

    public static Resource getFieldClass() {
        return  model.getProperty(BASE_URI + "Field");
    }

    public static Resource getLocalVariableClass() {
        return model.getProperty(BASE_URI + "Variable");
    }

    public static Property getThrowsProperty() {
        return model.getProperty(BASE_URI + "throws");
    }

    public static Property getContainsProperty() {
        return model.getProperty(BASE_URI + "contains");
    }

    /*public static Resource getAnnotationTypeIndividual () {
        return model.getProperty(BASE_URI + "AnnotationType");
    }*/

    public static Resource getLambdaIndividual () {
        return model.getResource(BASE_URI + "Lambda");
    }

    public static Property getLambdaImplementsProperty () {
        return model.getProperty(BASE_URI + "implements");
    }

    public static String getBaseURI() {
        return BASE_URI;
    }
}

