package io.github.bindglam.nationwar.database;

import com.bindglam.database.Database;
import com.bindglam.database.SQLiteDatabase;
import io.github.bindglam.nationwar.Context;
import io.github.bindglam.nationwar.Managerial;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

@Getter
public final class DatabaseManager implements Managerial {
    private Database<Connection, SQLException> sqlDatabase;

    @Override
    public void start(Context context) {
        // TODO : other types of database
        sqlDatabase = new SQLiteDatabase(new File(context.plugin().getDataFolder(), "database.db"), true, 10000);

        sqlDatabase.start();
    }

    @Override
    public void end(Context context) {
        sqlDatabase.stop();
    }
}
