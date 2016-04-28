package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Resource;
import org.codeontology.Ontology;
import spoon.reflect.declaration.ModifierKind;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum ModifierClass {
    PUBLIC (Ontology.PUBLIC_INDIVIDUAL, true),
    PRIVATE (Ontology.PRIVATE_INDIVIDUAL, true),
    PROTECTED (Ontology.PROTECTED_INDIVIDUAL, true),
    DEFAULT (Ontology.DEFAULT_INDIVIDUAL, true),
    ABSTRACT (Ontology.ABSTRACT_INDIVIDUAL, false),
    FINAL (Ontology.FINAL_INDIVIDUAL, false),
    STATIC (Ontology.STATIC_INDIVIDUAL, false),
    SYNCHRONYZED (Ontology.SYNCHRONIZED_INDIVIDUAL, false),
    VOLATILE (Ontology.VOLATILE_INDIVIDUAL, false);

    private Resource individual;
    private boolean accessModifier;

    ModifierClass(Resource individual, boolean accessModifier) {
        this.individual = individual;
        this.accessModifier = accessModifier;
    }

    public Resource getIndividual() {
        return individual;
    }

    public boolean isAccessModifier() {
        return accessModifier;
    }

    public static List<ModifierClass> asList(Set<ModifierKind> set) {
        List<ModifierClass> list = new ArrayList<>();
        for (ModifierKind current : set) {
            ModifierClass modifier = valueOf(current);
            if (modifier != null) {
                list.add(modifier);
            }
        }
        return list;
    }

    public static List<ModifierClass> asList(int code) {
        List<ModifierClass> list = new ArrayList<>();
        if (Modifier.isPublic(code)) {
            list.add(PUBLIC);
        } else if (Modifier.isPrivate(code)) {
            list.add(PRIVATE);
        } else if (Modifier.isProtected(code)) {
            list.add(PROTECTED);
        } else {
            list.add(DEFAULT);
        }

        if (Modifier.isAbstract(code)) {
            list.add(ABSTRACT);
        }

        if (Modifier.isFinal(code)) {
            list.add(FINAL);
        }

        if (Modifier.isStatic(code)) {
            list.add(STATIC);
        }

        if (Modifier.isSynchronized(code)) {
            list.add(SYNCHRONYZED);
        }

        if (Modifier.isVolatile(code)) {
            list.add(VOLATILE);
        }

        return list;
    }

    public static ModifierClass valueOf(ModifierKind modifier) {
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
                return SYNCHRONYZED;
            case VOLATILE:
                return VOLATILE;
            default:
                return null;
        }
    }
}
