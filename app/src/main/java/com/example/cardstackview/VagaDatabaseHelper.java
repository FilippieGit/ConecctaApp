package com.example.cardstackview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class VagaDatabaseHelper extends SQLiteOpenHelper {
    // Nome do banco de dados e versão
    private static final String DATABASE_NAME = "vagas.db";
    private static final int DATABASE_VERSION = 1;

    // Nomes das tabelas
    private static final String TABLE_VAGAS = "vagas_favoritas";
    private static final String TABLE_FAVORITOS = "favoritos";

    // Colunas comuns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_VAGA_ID = "vaga_id";

    // Colunas para a tabela de vagas favoritas
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_LOCALIZACAO = "localizacao";
    private static final String COLUMN_SALARIO = "salario";
    private static final String COLUMN_REQUISITOS = "requisitos";
    private static final String COLUMN_NIVEL_EXPERIENCIA = "nivel_experiencia";
    private static final String COLUMN_TIPO_CONTRATO = "tipo_contrato";
    private static final String COLUMN_AREA_ATUACAO = "area_atuacao";
    private static final String COLUMN_BENEFICIOS = "beneficios";
    private static final String COLUMN_VINCULO = "vinculo";
    private static final String COLUMN_RAMO = "ramo";
    private static final String COLUMN_EMPRESA_ID = "empresa_id";
    private static final String COLUMN_NOME_EMPRESA = "nome_empresa";
    private static final String COLUMN_HABILIDADES = "habilidades";

    public VagaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela de vagas favoritas
        String CREATE_TABLE_VAGAS = "CREATE TABLE " + TABLE_VAGAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VAGA_ID + " INTEGER UNIQUE,"
                + COLUMN_TITULO + " TEXT,"
                + COLUMN_DESCRICAO + " TEXT,"
                + COLUMN_LOCALIZACAO + " TEXT,"
                + COLUMN_SALARIO + " TEXT,"
                + COLUMN_REQUISITOS + " TEXT,"
                + COLUMN_NIVEL_EXPERIENCIA + " TEXT,"
                + COLUMN_TIPO_CONTRATO + " TEXT,"
                + COLUMN_AREA_ATUACAO + " TEXT,"
                + COLUMN_BENEFICIOS + " TEXT,"
                + COLUMN_VINCULO + " TEXT,"
                + COLUMN_RAMO + " TEXT,"
                + COLUMN_EMPRESA_ID + " INTEGER,"
                + COLUMN_NOME_EMPRESA + " TEXT,"
                + COLUMN_HABILIDADES + " TEXT)";
        db.execSQL(CREATE_TABLE_VAGAS);

        // Criação da tabela de favoritos (simplificada apenas para controle)
        String CREATE_TABLE_FAVORITOS = "CREATE TABLE " + TABLE_FAVORITOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VAGA_ID + " INTEGER UNIQUE)";
        db.execSQL(CREATE_TABLE_FAVORITOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VAGAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITOS);
        onCreate(db);
    }

    // Métodos para a tabela de favoritos
    public boolean adicionarVagaFavorita(Vagas vaga) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Adiciona à tabela de favoritos simples
            ContentValues favoritosValues = new ContentValues();
            favoritosValues.put(COLUMN_VAGA_ID, vaga.getVaga_id());
            long favoritosId = db.insertWithOnConflict(TABLE_FAVORITOS, null, favoritosValues, SQLiteDatabase.CONFLICT_IGNORE);

            // Adiciona à tabela completa de vagas favoritas
            ContentValues values = new ContentValues();
            values.put(COLUMN_VAGA_ID, vaga.getVaga_id());
            values.put(COLUMN_TITULO, vaga.getTitulo());
            values.put(COLUMN_DESCRICAO, vaga.getDescricao());
            values.put(COLUMN_LOCALIZACAO, vaga.getLocalizacao());
            values.put(COLUMN_SALARIO, vaga.getSalario());
            values.put(COLUMN_REQUISITOS, vaga.getRequisitos());
            values.put(COLUMN_NIVEL_EXPERIENCIA, vaga.getNivel_experiencia());
            values.put(COLUMN_TIPO_CONTRATO, vaga.getTipo_contrato());
            values.put(COLUMN_AREA_ATUACAO, vaga.getArea_atuacao());
            values.put(COLUMN_BENEFICIOS, vaga.getBeneficios());
            values.put(COLUMN_VINCULO, vaga.getVinculo());
            values.put(COLUMN_RAMO, vaga.getRamo());
            values.put(COLUMN_NOME_EMPRESA, vaga.getNome_empresa());
            values.put(COLUMN_HABILIDADES, "");

            long vagasId = db.insertWithOnConflict(TABLE_VAGAS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            return favoritosId != -1 && vagasId != -1;
        } catch (Exception e) {
            Log.e("DATABASE", "Erro ao adicionar vaga favorita", e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean isVagaFavorita(int vagaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITOS,
                new String[]{COLUMN_VAGA_ID},
                COLUMN_VAGA_ID + "=?",
                new String[]{String.valueOf(vagaId)},
                null, null, null);

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public void removerVagaFavorita(int vagaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITOS, COLUMN_VAGA_ID + "=?", new String[]{String.valueOf(vagaId)});
        db.delete(TABLE_VAGAS, COLUMN_VAGA_ID + "=?", new String[]{String.valueOf(vagaId)});
        db.close();
    }

    public List<Integer> getIdsVagasFavoritas() {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITOS,
                new String[]{COLUMN_VAGA_ID},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ids;
    }

    // Métodos para obter as vagas favoritas completas
    public List<Vagas> getAllVagasFavoritas() {
        List<Vagas> vagasList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_VAGA_ID,
                COLUMN_TITULO,
                COLUMN_DESCRICAO,
                COLUMN_LOCALIZACAO,
                COLUMN_SALARIO,
                COLUMN_REQUISITOS,
                COLUMN_NIVEL_EXPERIENCIA,
                COLUMN_TIPO_CONTRATO,
                COLUMN_AREA_ATUACAO,
                COLUMN_BENEFICIOS,
                COLUMN_VINCULO,
                COLUMN_RAMO,
                COLUMN_EMPRESA_ID,
                COLUMN_NOME_EMPRESA,
                COLUMN_HABILIDADES
        };

        Cursor cursor = db.query(TABLE_VAGAS,
                columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    // Create a new Vagas object using empty constructor and setters
                    Vagas vaga = new Vagas();
                    vaga.setVaga_id(getIntSafe(cursor, COLUMN_VAGA_ID));
                    vaga.setTitulo(getStringSafe(cursor, COLUMN_TITULO));
                    vaga.setDescricao(getStringSafe(cursor, COLUMN_DESCRICAO));
                    vaga.setLocalizacao(getStringSafe(cursor, COLUMN_LOCALIZACAO));
                    vaga.setSalario(getStringSafe(cursor, COLUMN_SALARIO));
                    vaga.setRequisitos(getStringSafe(cursor, COLUMN_REQUISITOS));
                    vaga.setNivel_experiencia(getStringSafe(cursor, COLUMN_NIVEL_EXPERIENCIA));
                    vaga.setTipo_contrato(getStringSafe(cursor, COLUMN_TIPO_CONTRATO));
                    vaga.setArea_atuacao(getStringSafe(cursor, COLUMN_AREA_ATUACAO));
                    vaga.setBeneficios(getStringSafe(cursor, COLUMN_BENEFICIOS));
                    vaga.setVinculo(getStringSafe(cursor, COLUMN_VINCULO));
                    vaga.setRamo(getStringSafe(cursor, COLUMN_RAMO));
                    vaga.setEmpresa_id(getIntSafe(cursor, COLUMN_EMPRESA_ID));
                    vaga.setNome_empresa(getStringSafe(cursor, COLUMN_NOME_EMPRESA));
                    vaga.setHabilidadesDesejaveisStr(getStringSafe(cursor, COLUMN_HABILIDADES));

                    // Set default values for missing fields
                    vaga.setId_usuario(0); // You might want to store this in the database if needed

                    vagasList.add(vaga);
                } catch (Exception e) {
                    Log.e("Database", "Erro ao ler vaga do banco de dados", e);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return vagasList;
    }
    // Métodos auxiliares para leitura segura
    private String getStringSafe(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return columnIndex != -1 ? cursor.getString(columnIndex) : "";
    }

    private int getIntSafe(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return columnIndex != -1 ? cursor.getInt(columnIndex) : 0;
    }
}