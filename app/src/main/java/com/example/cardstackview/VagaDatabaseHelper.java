package com.example.cardstackview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class VagaDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vagas_favoritas.db";
    private static final int DATABASE_VERSION = 1;

    // Nome da tabela e colunas
    public static final String TABLE_VAGAS = "vagas_favoritas";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VAGA_ID = "vaga_id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_DESCRICAO = "descricao";
    public static final String COLUMN_LOCALIZACAO = "localizacao";
    public static final String COLUMN_SALARIO = "salario";
    public static final String COLUMN_REQUISITOS = "requisitos";
    public static final String COLUMN_NIVEL_EXPERIENCIA = "nivel_experiencia";
    public static final String COLUMN_TIPO_CONTRATO = "tipo_contrato";
    public static final String COLUMN_AREA_ATUACAO = "area_atuacao";
    public static final String COLUMN_BENEFICIOS = "beneficios";
    public static final String COLUMN_VINCULO = "vinculo";
    public static final String COLUMN_RAMO = "ramo";
    public static final String COLUMN_EMPRESA_ID = "empresa_id";
    public static final String COLUMN_NOME_EMPRESA = "nome_empresa";
    public static final String COLUMN_HABILIDADES = "habilidades";

    public VagaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_VAGAS + "("
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
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VAGAS);
        onCreate(db);
    }

    // Adiciona uma vaga aos favoritos
    public void adicionarVagaFavorita(Vagas vaga) {
        SQLiteDatabase db = this.getWritableDatabase();

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
        values.put(COLUMN_EMPRESA_ID, vaga.getEmpresa_id());
        values.put(COLUMN_NOME_EMPRESA, vaga.getNome_empresa());
        values.put(COLUMN_HABILIDADES, vaga.getHabilidadesDesejaveisStr());

        db.insertWithOnConflict(TABLE_VAGAS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Remove uma vaga dos favoritos
    public void removerVagaFavorita(int vagaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VAGAS, COLUMN_VAGA_ID + " = ?", new String[]{String.valueOf(vagaId)});
        db.close();
    }

    // Obtém todas as vagas favoritas
    public List<Vagas> getAllVagasFavoritas() {
        List<Vagas> vagasList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_VAGAS, null, null, null, null, null, COLUMN_ID + " DESC");

            // Obter índices das colunas uma vez
            int idIndex = cursor.getColumnIndex(COLUMN_VAGA_ID);
            int tituloIndex = cursor.getColumnIndex(COLUMN_TITULO);
            int descricaoIndex = cursor.getColumnIndex(COLUMN_DESCRICAO);
            int localizacaoIndex = cursor.getColumnIndex(COLUMN_LOCALIZACAO);
            int salarioIndex = cursor.getColumnIndex(COLUMN_SALARIO);
            int requisitosIndex = cursor.getColumnIndex(COLUMN_REQUISITOS);
            int nivelExpIndex = cursor.getColumnIndex(COLUMN_NIVEL_EXPERIENCIA);
            int tipoContratoIndex = cursor.getColumnIndex(COLUMN_TIPO_CONTRATO);
            int areaAtuacaoIndex = cursor.getColumnIndex(COLUMN_AREA_ATUACAO);
            int beneficiosIndex = cursor.getColumnIndex(COLUMN_BENEFICIOS);
            int vinculoIndex = cursor.getColumnIndex(COLUMN_VINCULO);
            int ramoIndex = cursor.getColumnIndex(COLUMN_RAMO);
            int empresaIdIndex = cursor.getColumnIndex(COLUMN_EMPRESA_ID);
            int nomeEmpresaIndex = cursor.getColumnIndex(COLUMN_NOME_EMPRESA);
            int habilidadesIndex = cursor.getColumnIndex(COLUMN_HABILIDADES);

            if (cursor.moveToFirst()) {
                do {
                    Vagas vaga = new Vagas(
                            idIndex != -1 ? cursor.getInt(idIndex) : 0,
                            tituloIndex != -1 ? cursor.getString(tituloIndex) : "",
                            descricaoIndex != -1 ? cursor.getString(descricaoIndex) : "",
                            localizacaoIndex != -1 ? cursor.getString(localizacaoIndex) : "",
                            salarioIndex != -1 ? cursor.getString(salarioIndex) : "",
                            requisitosIndex != -1 ? cursor.getString(requisitosIndex) : "",
                            nivelExpIndex != -1 ? cursor.getString(nivelExpIndex) : "",
                            tipoContratoIndex != -1 ? cursor.getString(tipoContratoIndex) : "",
                            areaAtuacaoIndex != -1 ? cursor.getString(areaAtuacaoIndex) : "",
                            beneficiosIndex != -1 ? cursor.getString(beneficiosIndex) : "",
                            vinculoIndex != -1 ? cursor.getString(vinculoIndex) : "",
                            ramoIndex != -1 ? cursor.getString(ramoIndex) : "",
                            empresaIdIndex != -1 ? cursor.getInt(empresaIdIndex) : 0,
                            nomeEmpresaIndex != -1 ? cursor.getString(nomeEmpresaIndex) : "",
                            habilidadesIndex != -1 ? cursor.getString(habilidadesIndex) : ""
                    );
                    vagasList.add(vaga);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return vagasList;
    }

    // Verifica se uma vaga já está favoritada
    public boolean isVagaFavorita(int vagaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VAGAS,
                new String[]{COLUMN_VAGA_ID},
                COLUMN_VAGA_ID + " = ?",
                new String[]{String.valueOf(vagaId)},
                null, null, null);

        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isFavorite;
    }
}
