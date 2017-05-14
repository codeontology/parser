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

package org.codeontology.docparser;


public class TagFactory {
    private static TagFactory instance;

    private TagFactory() {

    }

    public static TagFactory getInstance() {
        if (instance == null) {
            instance = new TagFactory();
        }

        return instance;
    }

    public Tag createTag(String name, String text) {
        switch (name) {
            case ParamTag.TAG:
                return new ParamTag(text);
            case ReturnTag.TAG:
                return new ReturnTag(text);
            default:
                return new Tag(name, text);
        }
    }
}