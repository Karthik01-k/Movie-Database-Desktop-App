package com.moviedb.model;

public class Cast {
    private int id;
    private String name;
    private String character;
    private String profilePath;
    private String department;  // for crew: Directing, Writing, etc.
    private String job;         // for crew: Director, Writer, etc.

    public Cast() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }

    public String getCharacter()                        { return character; }
    public void setCharacter(String character)          { this.character = character; }

    public String getProfilePath()                      { return profilePath; }
    public void setProfilePath(String profilePath)      { this.profilePath = profilePath; }

    public String getDepartment()                       { return department; }
    public void setDepartment(String department)        { this.department = department; }

    public String getJob()                              { return job; }
    public void setJob(String job)                      { this.job = job; }
}
