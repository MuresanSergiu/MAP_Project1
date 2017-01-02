package afterwind.lab1.repository;

import afterwind.lab1.database.SQLiteDatabase;
import afterwind.lab1.entity.Candidate;
import afterwind.lab1.entity.Section;
import afterwind.lab1.exception.ValidationException;
import afterwind.lab1.repository.Repository;
import afterwind.lab1.repository.SQLiteRepository;
import afterwind.lab1.service.CandidateService;
import afterwind.lab1.service.SectionService;
import afterwind.lab1.validator.IValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteSectionRepository extends SQLiteRepository<Section, Integer> {

    private SQLiteDatabase database;
    private PreparedStatement statementAdd, statementRemove, statementUpdate;

    public SQLiteSectionRepository(SQLiteDatabase database, IValidator<Section> validator) {
        super(database, validator);
        this.database = database;

        statementAdd = database.getStatement("INSERT INTO Sections VALUES(?, ?, ?)");
        statementRemove = database.getStatement("DELETE FROM Sections WHERE ID = ?");
        statementUpdate = database.getStatement("UPDATE Sections SET Name = ?, Seats = ? WHERE ID = ?");
        statementSelectAll = database.getStatement("SELECT * FROM Sections");

        load();
    }

    private void load() {
        try {
            ResultSet result = statementSelectAll.executeQuery();
            while(result.next()) {
                Section s = new Section(result.getInt(1), result.getString(2), result.getInt(3));
                try {
                    super.add(s);
                } catch (ValidationException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Section s) throws ValidationException {
        try {
            statementAdd.setInt(1, s.getId());
            statementAdd.setString(2, s.getName());
            statementAdd.setInt(3, s.getNrLoc());
            statementAdd.execute();
            super.add(s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Section s) {
        try {
            statementRemove.setInt(1, s.getId());
            statementRemove.execute();
            super.remove(s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Integer key, Section data) {
        try {
            statementUpdate.setString(1, data.getName());
            statementUpdate.setInt(2, data.getNrLoc());
            statementUpdate.setInt(3, key);
            statementUpdate.execute();
            super.update(key, data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
