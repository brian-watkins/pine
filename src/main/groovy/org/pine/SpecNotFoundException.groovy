package org.pine

class SpecNotFoundException extends RuntimeException {

    public SpecNotFoundException() {
        super("No Specs Found. Annotate a method with @Describe('specName').");
    }

}
