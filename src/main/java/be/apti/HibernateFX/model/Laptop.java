package be.apti.HibernateFX.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "laptops")
public class Laptop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vendor;
    private String type;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    public Laptop() {
    }

    public Laptop(String vendor, String type, LocalDate releaseDate) {
        this.vendor = vendor;
        this.type = type;
        this.releaseDate = releaseDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Laptop{" +
                "id=" + id +
                ", vendor='" + vendor + '\'' +
                ", type='" + type + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }

    public long getId() {
        return id;
    }
}