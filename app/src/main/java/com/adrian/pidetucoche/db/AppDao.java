package com.adrian.pidetucoche.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class AppDao {

    @Query("SELECT * FROM Usuario WHERE email = :email")
    public abstract Usuario comprobarEmailDisponible(String email);

    @Insert
    public abstract void InsertarUsuario(Usuario usuario);

    @Query("SELECT * FROM Usuario")
    public abstract List<Usuario> getAllUsers();


    @Query("SELECT * FROM Usuario WHERE email = :email AND password =:password")
    public abstract Usuario getUsername(String email, String password);

}
