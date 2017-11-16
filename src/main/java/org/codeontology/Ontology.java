/*
Copyright 2017 Mattia Atzeni, Maurizio Atzori

This file is part of CodeOntology.

CodeOntology is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CodeOntology is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CodeOntology.  If not, see <http://www.gnu.org/licenses/>
*/

package org.codeontology;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Ontology {

    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String WOC = "http://rdf.webofcode.org/woc/";

    private static final Model model = ontology();

    private static Model ontology() {
        try {
            File ontology = new File(System.getProperty("user.dir") + "/ontology/CodeOntology.owl");
            FileInputStream reader = new FileInputStream(ontology);
            return ModelFactory.createDefaultModel().read(reader, "");
        } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
        }
    }

    public static Model getModel() {
        return model;
    }

    public static final Resource PACKAGE_ENTITY = model.getResource(WOC + "Package");

    public static final Resource CLASS_ENTITY = model.getResource(WOC + "Class");

    public static final Resource INTERFACE_ENTITY = model.getResource(WOC + "Interface");

    public static final Resource ENUM_ENTITY = model.getResource(WOC + "Enum");

    public static final Resource ANNOTATION_ENTITY = model.getResource(WOC + "Annotation");

    public static final Resource PRIMITIVE_ENTITY = model.getResource(WOC + "PrimitiveType");

    public static final Resource ARRAY_ENTITY = model.getResource(WOC + "ArrayType");

    public static final Resource TYPE_VARIABLE_ENTITY =  model.getResource(WOC + "TypeVariable");

    public static final Resource PARAMETERIZED_TYPE_ENTITY =  model.getResource(WOC + "ParameterizedType");

    public static final Resource FIELD_ENTITY =  model.getResource(WOC + "Field");

    public static final Resource CONSTRUCTOR_ENTITY = model.getResource(WOC + "Constructor");

    public static final Resource METHOD_ENTITY = model.getResource(WOC + "Method");

    public static final Resource PARAMETER_ENTITY = model.getResource(WOC + "Parameter");

    public static final Resource LOCAL_VARIABLE_ENTITY = model.getResource(WOC + "LocalVariable");

    public static final Resource LAMBDA_ENTITY = model.getResource(WOC + "LambdaExpression");

    public static final Resource ANONYMOUS_CLASS_ENTITY = model.getResource(WOC + "AnonymousClass");

    public static final Resource TYPE_ARGUMENT_ENTITY = model.getResource(WOC + "TypeArgument");

    public static final Resource WILDCARD_ENTITY = model.getResource(WOC + "Wildcard");

    public static final Resource PROJECT_ENTITY = model.getResource(WOC + "Project");

    public static final Resource MAVEN_PROJECT_ENTITY = model.getResource(WOC + "MavenProject");

    public static final Resource GRADLE_PROJECT_ENTITY = model.getResource(WOC + "GradleProject");

    public static final Resource JAR_FILE_ENTITY = model.getResource(WOC + "JarFile");

    public static final Resource STATEMENT_ENTITY = model.getResource(WOC + "Statement");

    public static final Resource BLOCK_ENTITY = model.getResource(WOC + "BlockStatement");

    public static final Resource IF_THEN_ELSE_ENTITY = model.getResource(WOC + "IfThenElseStatement");

    public static final Resource SWITCH_ENTITY = model.getResource(WOC + "SwitchStatement");

    public static final Resource CASE_ENTITY = model.getResource(WOC + "CaseLabeledBlock");

    public static final Resource DEFAULT_ENTITY = model.getResource(WOC + "DefaultLabeledBlock");

    public static final Resource WHILE_ENTITY = model.getResource(WOC + "WhileStatement");

    public static final Resource DO_WHILE_ENTITY = model.getResource(WOC + "DoStatement");

    public static final Resource FOR_ENTITY = model.getResource(WOC + "ForStatement");

    public static final Resource FOR_EACH_ENTITY = model.getResource(WOC + "ForEachStatement");

    public static final Resource TRY_ENTITY = model.getResource(WOC + "TryStatement");

    public static final Resource RETURN_ENTITY = model.getResource(WOC + "ReturnStatement");

    public static final Resource THROW_ENTITY = model.getResource(WOC + "ThrowSatement");

    public static final Resource BREAK_ENTITY = model.getResource(WOC + "BreakStatement");

    public static final Resource CONTINUE_ENTITY = model.getResource(WOC + "ContinueStatement");

    public static final Resource ASSERT_ENTITY = model.getResource(WOC + "AssertStatement");

    public static final Resource SYNCHRONIZED_ENTITY = model.getResource(WOC + "SynchronizedStatement");

    public static final Resource LOCAL_VARIABLE_DECLARATION_ENTITY = model.getResource(WOC + "LocalVariableDeclarationStatement");

    public static final Resource CLASS_DECLARATION_ENTITY = model.getResource(WOC + "ClassDeclarationStatement");

    public static final Resource EXPRESSION_STATEMENT_ENTITY = model.getResource(WOC + "ExpressionStatement");

    public static final Resource STATEMENT_EXPRESSION_LIST_ENTITY = model.getResource(WOC + "StatementExpressionList");

    public static final Resource CATCH_ENTITY = model.getResource(WOC + "CatchBlock");

    public static final Resource FINALLY_ENTITY = model.getResource(WOC + "FinallyBlock");

    public static final Resource EXPRESSION_ENTITY = model.getResource(WOC + "Expression");

    public static final Resource ASSIGNMENT_EXPRESSION_ENTITY = model.getResource(WOC + "AssignmentExpression");

    public static final Resource METHOD_INVOCATION_EXPRESSION_ENTITY = model.getResource(WOC + "MethodInvocationExpression");

    public static final Resource ACTUAL_ARGUMENT_ENTITY = model.getResource(WOC + "ActualArgument");

    public static final Resource CLASS_INSTANCE_CREATION_EXPRESSION_ENTITY = model.getResource(WOC + "ClassInstanceCreationExpression");


    public static final Property RDF_TYPE_PROPERTY = model.getProperty(RDF + "type");

    public static final Property RDFS_LABEL_PROPERTY = model.getProperty(RDFS + "label");

    public static final Property JAVA_TYPE_PROPERTY = model.getProperty(WOC + "hasType");

    public static final Property COMMENT_PROPERTY = model.getProperty(RDFS + "comment");

    public static final Property NAME_PROPERTY = model.getProperty(WOC + "hasName");

    public static final Property SIMPLE_NAME_PROPERTY = model.getProperty(WOC + "hasSimpleName");

    public static final Property CANONICAL_NAME_PROPERTY = model.getProperty(WOC + "hasCanonicalName");

    public static final Property DECLARED_BY_PROPERTY = model.getProperty(WOC + "isDeclaredBy");

    public static final Property HAS_PACKAGE_PROPERTY = model.getProperty(WOC + "hasPackage");

    public static final Property IS_PACKAGE_OF_PROPERTY = model.getProperty(WOC + "isPackageOf");

    public static final Property HAS_CONSTRUCTOR_PROPERTY = model.getProperty(WOC + "hasConstructor");

    public static final Property HAS_METHOD_PROPERTY = model.getProperty(WOC + "hasMethod");

    public static final Property HAS_FIELD_PROPERTY = model.getProperty(WOC + "hasField");

    public static final Property RETURN_TYPE_PROPERTY = model.getProperty(WOC + "hasReturnType");

    public static final Property RETURNS_VAR_PROPERTY = model.getProperty(WOC + "returns");

    public static final Property RETURN_DESCRIPTION_PROPERTY = model.getProperty(WOC + "hasReturnDescription");

    public static final Property CONSTRUCTS_PROPERTY = model.getProperty(WOC + "constructs");

    public static final Property PARAMETER_PROPERTY = model.getProperty(WOC + "hasParameter");

    public static final Property POSITION_PROPERTY = model.getProperty(WOC + "hasPosition");

    public static final Property SOURCE_CODE_PROPERTY = model.getProperty(WOC + "hasSourceCode");

    public static final Property THROWS_PROPERTY = model.getProperty(WOC + "throws");

    public static final Property MODIFIER_PROPERTY = model.getProperty(WOC + "hasModifier");

    public static final Property REFERENCES_PROPERTY = model.getProperty(WOC + "references");

    public static final Property EXTENDS_PROPERTY = model.getProperty(WOC + "extends");

    public static final Property IMPLEMENTS_PROPERTY = model.getProperty(WOC + "implements");

    public static final Property SUPER_PROPERTY = model.getProperty(WOC + "hasSuperBound");

    public static final Property ARRAY_OF_PROPERTY = model.getProperty(WOC + "isArrayOf");

    public static final Property DIMENSIONS_PROPERTY = model.getProperty(WOC + "hasDimensions");

    public static final Property FORMAL_TYPE_PARAMETER_PROPERTY = model.getProperty(WOC + "hasFormalTypeParameter");

    public static final Property ACTUAL_TYPE_ARGUMENT_PROPERTY = model.getProperty(WOC + "hasActualTypeArgument");

    public static final Property GENERIC_TYPE_PROPERTY = model.getProperty(WOC + "hasGenericType");

    public static final Property ANNOTATION_PROPERTY = model.getProperty(WOC + "hasAnnotation");

    public static final Property OVERRIDES_PROPERTY = model.getProperty(WOC + "overrides");

    public static final Property VAR_ARGS_PROPERTY = model.getProperty(WOC + "isVarArgs");

    public static final Property PROJECT_PROPERTY = model.getProperty(WOC + "hasProject");

    public static final Property SUBPROJECT_PROPERTY = model.getProperty(WOC + "hasSubProject");

    public static final Property BUILD_FILE_PROPERTY = model.getProperty(WOC + "hasBuildFile");

    public static final Property DEPENDENCY_PROPERTY = model.getProperty(WOC + "hasDependency");

    public static final Property LINE_PROPERTY = model.getProperty(WOC + "hasLine");

    public static final Property NEXT_PROPERTY = model.getProperty(WOC + "hasNextStatement");

    public static final Property CONDITION_PROPERTY = model.getProperty(WOC + "hasCondition");

    public static final Property STATEMENT_PROPERTY = model.getProperty(WOC + "hasSubStatement");

    public static final Property THEN_PROPERTY = model.getProperty(WOC + "hasThenBranch");

    public static final Property ELSE_PROPERTY = model.getProperty(WOC + "hasElseBranch");

    public static final Property BODY_PROPERTY = model.getProperty(WOC + "hasBody");

    public static final Property END_LINE_PROPERTY = model.getProperty(WOC + "hasEndLine");

    public static final Property FOR_INIT_PROPERTY = model.getProperty(WOC + "hasForInit");

    public static final Property FOR_UPDATE_PROPERTY = model.getProperty(WOC + "hasForUpdate");

    public static final Property EXPRESSION_PROPERTY = model.getProperty(WOC + "hasSubExpression");

    public static final Property RETURNED_EXPRESSION_PROPERTY = model.getProperty(WOC + "hasReturnedExpression");

    public static final Property THROWN_EXPRESSION_PROPERTY = model.getProperty(WOC + "hasThrownExpression");

    public static final Property ASSERT_EXPRESSION_PROPERTY = model.getProperty(WOC + "hasAssertExpression");

    public static final Property VARIABLE_PROPERTY = model.getProperty(WOC + "hasVariable");

    public static final Property CATCH_CLAUSE_PROPERTY = model.getProperty(WOC + "hasCatchClause");

    public static final Property CATCH_FORMAL_PARAMETER_PROPERTY = model.getProperty(WOC + "hasCatchFormalParameter");

    public static final Property FINALLY_CLAUSE_PROPERTY = model.getProperty(WOC + "hasFinallyClause");

    public static final Property RESOURCE_PROPERTY = model.getProperty(WOC + "hasResource");

    public static final Property TARGETED_LABEL_PROPERTY = model.getProperty(WOC + "hasTargetedLabel");

    public static final Property WOC_LABEL_PROPERTY = model.getProperty(WOC + "hasLabel");

    public static final Property INITIALIZER_PROPERTY = model.getProperty(WOC + "hasInitializer");

    public static final Property DECLARATION_PROPERTY = model.getProperty(WOC + "hasDeclaration");

    public static final Property SWITCH_LABEL_PROPERTY = model.getProperty(WOC + "hasSwitchLabel");

    public static final Property LEFT_HAND_SIDE_PROPERTY = model.getProperty(WOC + "hasLeftHandSide");

    public static final Property INVOKES_PROPERTY = model.getProperty(WOC + "invokes");

    public static final Property ARGUMENT_PROPERTY = model.getProperty(WOC + "hasArgument");

    public static final Property TARGET_PROPERTY = model.getProperty(WOC + "hasTarget");


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
