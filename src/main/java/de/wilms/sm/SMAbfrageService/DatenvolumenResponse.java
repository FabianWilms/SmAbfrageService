package de.wilms.sm.SMAbfrageService;

public class DatenvolumenResponse {
    private double inclusive;
    private double available;
    private double used;
    public final String UNIT = "megabyte";

    public DatenvolumenResponse() {
    }

    public DatenvolumenResponse(double inclusiveDatavolumeInMB, double availableDatavolumeInMB, double usedDatavolumeInMB) {
        this.inclusive = inclusiveDatavolumeInMB;
        this.available = availableDatavolumeInMB;
        this.used = usedDatavolumeInMB;
    }

    public double getInclusive() {
        return inclusive;
    }

    public void setInclusive(double inclusive) {
        this.inclusive = inclusive;
    }

    public double getAvailable() {
        return available;
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    public double getUsed() {
        return used;
    }

    public void setUsed(double used) {
        this.used = used;
    }
}
