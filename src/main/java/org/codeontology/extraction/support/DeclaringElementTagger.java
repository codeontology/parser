package org.codeontology.extraction.support;

import org.codeontology.Ontology;
import org.codeontology.extraction.RDFLogger;

public class DeclaringElementTagger {

    private MemberEntity<?> member;

    public DeclaringElementTagger(MemberEntity<?> member) {
        this.member = member;
    }

    public void tagDeclaredBy() {
        RDFLogger.getInstance().addTriple(member, Ontology.DECLARED_BY_PROPERTY, member.getDeclaringElement());
    }
}
