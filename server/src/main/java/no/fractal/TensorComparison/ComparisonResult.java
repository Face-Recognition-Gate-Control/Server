package no.fractal.TensorComparison;

import java.util.UUID;

public class ComparisonResult {
    public final UUID id;
    public final float diffValue;

    public ComparisonResult(UUID id, float diffValue) {
        this.id = id;
        this.diffValue = diffValue;
    }
}
