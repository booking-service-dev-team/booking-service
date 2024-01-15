package mate.academy.bookingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "accommodations")
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Address address;
    @Column(name = "size_of_accommodation")
    private String sizeOfAccommodation;
    /*
    Instead of using an array, a string is employed, as it corresponds to the format of input data
    from a potential customer's database. In future versions, the approach can be modified to use
    a collection for implementing functionality for filtering based on these "amenities."
    P.S. Additionally, for the proper addition of "amenities" to a separate table, the use of
    the Levenshtein algorithm can be implemented.
     */
    @Column(name = "amenities")
    private String amenities;
    @Column(name = "price_per_month_usd")
    private BigDecimal pricePerMonthUsd;
    // Number of available units of this accommodation
    @Column(name = "number_of_available_accommodation")
    private Integer numberOfAvailableAccommodation;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    public enum Type {
        HOUSE,
        APARTMENT,
        CONDO,
        VACATION_HOME,
        CATTAILS,
        TV_BOX
    }
}
