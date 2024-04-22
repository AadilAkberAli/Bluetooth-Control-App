package com.example.bluetoothserver.Broucher;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bluetoothserver.database.Broucher;

import java.util.List;

@Dao
public interface BroucherDao {

    @Query("SELECT * FROM Broucher")
    List<Broucher> loadAllBrouchers();

    @Query("SELECT * FROM Broucher where datetime(expiryDate) >= datetime(:currentDate)")
    LiveData<List<Broucher>> loadAllAvailableBrouchers(String currentDate);

    @Query("SELECT * FROM Broucher where datetime(expiryDate) >= datetime(:currentDate) AND isMerged == :isMerge")
    List<Broucher> loadAllAvailableBrouchersIsMerged(String currentDate,boolean isMerge);

    @Query("Delete From Broucher where datetime(expiryDate) < datetime(:currentDate)")
    void deleteAllExpireBrochures(String currentDate);

    @Query("SELECT * From Broucher where datetime(expiryDate) < datetime(:currentDate)")
    List<Broucher> getAllExpireBrochures(String currentDate);

    @Insert
    void insert(Broucher broucher);


    @Query("UPDATE broucher SET exist = :exist WHERE filesName = :name")
    int updateBroucherByFileName( boolean exist, String name);

    @Delete
    void delete(Broucher broucher);

    @Query("DELETE FROM broucher WHERE productName = :fileName")
    void deleteByFileName(String fileName);

    @Query("DELETE from broucher WHERE isMerged == :isMerged")
    void deleteAll(boolean isMerged);

    @Query("SELECT EXISTS(SELECT * FROM broucher WHERE filesName = :fileName)")
    Boolean isRowIsExist(String fileName);

}
