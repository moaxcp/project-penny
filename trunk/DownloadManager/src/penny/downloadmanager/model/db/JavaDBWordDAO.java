/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package penny.downloadmanager.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author john
 */
public class JavaDBWordDAO implements WordDAO {

    @Override
    public void addWord(UUID uuid, String word) {

        Connection connection = JavaDBDataSource.getInstance().getConnection();
        int executeUpdate = 0;
        long wordIndex = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "select max(wordindex) from word where " + Download.PROP_ID + " = '" + uuid + "'";
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, query);
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                wordIndex = rs.getLong(1) + 1;
            }
            PreparedStatement insert = connection.prepareStatement("insert into word\n(" + Download.PROP_ID + ", word, wordindex)\nvalues\n(?, ?, ?)");
            insert.setString(1, uuid.toString());
            insert.setString(2, word.replace("'", "''"));
            insert.setLong(3, wordIndex);
            executeUpdate = insert.executeUpdate();
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, "returned {0} on insert into word (" + Download.PROP_ID + ", word, wordindex) values ({1}, {2}, {3})", new Object[]{executeUpdate, uuid, word, wordIndex});
            insert.close();

            if (executeUpdate != 1) {
                throw new IllegalStateException("executeUpdate is " + executeUpdate + " there should be 1 insert");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.SEVERE, "Exception saving word " + uuid + " " + word, ex);
            throw new IllegalStateException("There was an SQL Exception", ex);
        } finally {
            JavaDBDataSource.getInstance().returnConnection(connection);
        }
    }

    @Override
    public void addWords(UUID uuid, List<String> words) {

        Connection connection = JavaDBDataSource.getInstance().getConnection();
        int executeUpdate = 0;
        long wordIndex = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "select max(wordindex) from word where " + Download.PROP_ID + " = '" + uuid + "'";
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, query);
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                wordIndex = rs.getLong(1) + 1;
            }
            StringBuilder insert = new StringBuilder();
            if(words.size() > 0) {
                insert.append("insert into word\n(" + Download.PROP_ID + ", word, wordindex)\nvalues\n('").append(uuid.toString()).append("', '").append(words.get(0).replace("'", "''")).append("', ").append(wordIndex++).append(")");
            for (int i = 1; i < words.size(); i++) {
                insert.append(",\n('").append(uuid.toString()).append("', '").append(words.get(i).replace("'", "''")).append("', ").append(wordIndex++).append(")");
            }
                executeUpdate = statement.executeUpdate(insert.toString());
                Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, "returned {0} on {1}", new Object[]{executeUpdate, insert.toString()});

                if (executeUpdate != words.size()) {
                    throw new IllegalStateException("executeUpdate is " + executeUpdate + " there should be 1 insert");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.SEVERE, "Exception saving words " + uuid + " " + words, ex);
            throw new IllegalStateException("There was an SQL Exception", ex);
        } finally {
            JavaDBDataSource.getInstance().returnConnection(connection);
        }
    }

    @Override
    public void deleteWords(UUID uuid) {
        Connection connection = JavaDBDataSource.getInstance().getConnection();
        try {
            int executeUpdate = 0;
            Statement statement = connection.createStatement();
            String query = "delete from word where " + Download.PROP_ID + " = '" + uuid + "'";
            executeUpdate = statement.executeUpdate(query);
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, "returned {0} on {1}", new Object[]{executeUpdate, query});
        } catch (SQLException ex) {
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException("There was an SQL Exception", ex);
        } finally {
            JavaDBDataSource.getInstance().returnConnection(connection);
        }
    }

    @Override
    public void deleteWords(UUID uuid, List<String> words) {
        Connection connection = JavaDBDataSource.getInstance().getConnection();
        try {
            for (String word : words) {
                int executeUpdate = 0;
                Statement statement = connection.createStatement();
                String query = "delete from word where " + Download.PROP_ID + " = '" + uuid + "' and word = '" + word.replace("'", "''") + "'";
                executeUpdate = statement.executeUpdate(query);
                Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, "returned {0} on {1}", new Object[]{executeUpdate, query});
            }
        } catch (SQLException ex) {
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException("There was an SQL Exception", ex);
        } finally {
            JavaDBDataSource.getInstance().returnConnection(connection);
        }
    }

    @Override
    public long getWordCount() {
        Connection connection = JavaDBDataSource.getInstance().getConnection();
        long count = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "select count(*) from word";
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.FINE, query);
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                count = rs.getLong(1);
                statement.close();
                return count;
            } else {
                return count;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JavaDBWordDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JavaDBDataSource.getInstance().returnConnection(connection);
        }
        return count;
    }
}
