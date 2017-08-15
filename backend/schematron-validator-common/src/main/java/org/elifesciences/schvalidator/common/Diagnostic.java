package org.elifesciences.schvalidator.common;

public final class Diagnostic {

    private final String context;
    private final String message;
    private final DiagnosticLevel level;

    public Diagnostic(String context, String message, DiagnosticLevel level) {
        this.context = context;
        this.message = message;
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diagnostic that = (Diagnostic) o;

        if (!context.equals(that.context)) return false;
        if (!message.equals(that.message)) return false;
        return level == that.level;
    }

    public String getContext() {
        return context;
    }

    public DiagnosticLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        int result = context.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + level.hashCode();
        return result;
    }
}
