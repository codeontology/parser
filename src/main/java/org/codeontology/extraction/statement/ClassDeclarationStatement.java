package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.declaration.ExecutableEntity;
import org.codeontology.extraction.declaration.TypeEntity;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

public class ClassDeclarationStatement extends StatementEntity<CtClass<?>> {
    public ClassDeclarationStatement(CtClass<?> element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.CLASS_DECLARATION_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagDeclaredClass();
    }

    public TypeEntity<?> getDeclaredClass() {
        TypeEntity<?> type = getFactory().wrap((CtType<?>) getElement());
        type.setParent(getParent(ExecutableEntity.class, TypeEntity.class));
        return type;
    }

    private void tagDeclaredClass() {
        TypeEntity<?> declaredClass = getDeclaredClass();
        getLogger().addTriple(declaredClass, Ontology.DECLARATION_PROPERTY, this);
        declaredClass.extract();
    }
}
