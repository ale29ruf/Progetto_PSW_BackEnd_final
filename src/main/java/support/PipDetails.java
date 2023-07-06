package support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Il seguente DTO non è ridondante perchè stiamo facendo un contratto di acquisto in base al prezzo e alla quantità che l'utente ha visto.
 * Ovviamente se ci sono cambiamenti l'acquisto fallisce
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PipDetails {

    private int id;
    private int pid;
    private int qta;
    private float price;

    public PipDetails() {}

    public PipDetails(int pid, int qta, float price) {
        this.pid = pid;
        this.qta = qta;
        this.price = price;
    }

}
