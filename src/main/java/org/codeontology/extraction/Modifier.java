package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.ModifierKind;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum Modifier {
    PUBLIC (Ontology.PUBLIC_INDIVIDUAL, true),
    PRIVATE (Ontology.PRIVATE_INDIVIDUAL, true),
    PROTECTED (Ontology.PROTECTED_INDIVIDUAL, true),
    DEFAULT (Ontology.DEFAULT_INDIVIDUAL, true),
    ABSTRACT (Ontology.ABSTRACT_INDIVIDUAL, false),
    FINAL (Ontology.FINAL_INDIVIDUAL, false),
    STATIC (Ontology.STATIC_INDIVIDUAL, false),
    SYNCHRONIZED (Ontology.SYNCHRONIZED_INDIVIDUAL, false),
    VOLATILE (Ontology.VOLATILE_INDIVIDUAL, false);

    private Resource individual;
    private boolean accessModifier;

    Modifier(Resource individual, boolean accessModifier) {
        this.individual = individual;
        this.accessModifier = accessModifier;
    }

    public Resource getIndividual() {
        return individual;
    }

    public boolean isAccessModifier() {
        return accessModifier;
    }

    public static List<Modifier> asList(Set<ModifierKind> set) {
        List<Modifier> list = new ArrayList<>();
        for (ModifierKind current : set) {
            Modifier modifier = valueOf(current);
            if (modifier != null) {
                list.add(modifier);
            }
        }
        return list;
    }

    public static List<Modifier> asList(int code) {
        List<Modifier> list = new ArrayList<>();
        if (java.lang.reflect.Modifier.isPublic(code)) {
            list.add(PUBLIC);
        } else if (java.lang.reflect.Modifier.isPrivate(code)) {
            list.add(PRIVATE);
        } else if (java.lang.reflect.Modifier.isProtected(code)) {
            list.add(PROTECTED);
        } else {
            list.add(DEFAULT);
        }

        if (java.lang.reflect.Modifier.isAbstract(code)) {
            list.add(ABSTRACT);
        }

        if (java.lang.reflect.Modifier.isFinal(code)) {
            list.add(FINAL);
        }

        if (java.lang.reflect.Modifier.isStatic(code)) {
            list.add(STATIC);
        }

        if (java.lang.reflect.Modifier.isSynchronized(code)) {
            list.add(SYNCHRONIZED);
        }

        if (java.lang.reflect.Modifier.isVolatile(code)) {
            list.add(VOLATILE);
        }

        return list;
    }

    public static Modifier valueOf(ModifierKind modifier) {
        if (modifier != null) {
            switch (modifier) {
                case PUBLIC:
                    return PUBLIC;
                case PRIVATE:
                    return PRIVATE;
                case PROTECTED:
                    return PROTECTED;
                case ABSTRACT:
                    return ABSTRACT;
                case FINAL:
                    return FINAL;
                case STATIC:
                    return STATIC;
                case SYNCHRONIZED:
                    return SYNCHRONIZED;
                case VOLATILE:
                    return VOLATILE;
            }
        }

        return null;
    }
}
