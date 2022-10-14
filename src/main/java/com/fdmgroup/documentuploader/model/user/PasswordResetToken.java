package com.fdmgroup.documentuploader.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Token generated upon receiving a request to reset the password of a {@link User}.
 * This token must be used to successfully reset the password of any User.
 *
 * @author Noah Anderson
 */
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PASSWORD_RESET_TOKEN_GEN")
    @SequenceGenerator(name = "PASSWORD_RESET_TOKEN_GEN", sequenceName = "PASSWORD_RESET_TOKEN_ID_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "token", unique = true, nullable = false, updatable = false)
    private String token;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    private boolean isUsed;

    public PasswordResetToken() {
        super();
    }

    public PasswordResetToken(User user) {
        super();
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = getExpiryDate(EXPIRATION);
    }

    private Date getExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);

        return new Date(cal.getTime().getTime());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public boolean isExpired() {
        return ChronoUnit.DAYS.between(getExpiryDate(), LocalDate.now()) >= 1;
    }

    private LocalDate getExpiryDate() {
        return this.expiryDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
