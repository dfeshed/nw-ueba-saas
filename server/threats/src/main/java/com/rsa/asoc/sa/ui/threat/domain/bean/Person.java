package com.rsa.asoc.sa.ui.threat.domain.bean;

/**
 * Assignee or the creator of an incident
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class Person {
    private Long id;
    private String name;
    private String login;
    private String emailAddress;

    public Person() {
    }

    public Person(Long id, String name, String login, String emailAddress) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.emailAddress = emailAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
