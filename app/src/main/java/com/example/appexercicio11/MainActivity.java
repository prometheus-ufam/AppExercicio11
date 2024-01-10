package com.example.appexercicio11;// MainActivity.java
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNome, editTextNumero, editTextEndereco;
    private Button salvaButton, loadButton;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNome = findViewById(R.id.editText1);
        editTextNumero = findViewById(R.id.editText2);
        editTextEndereco = findViewById(R.id.editText3);
        salvaButton = findViewById(R.id.salvaButton);
        loadButton = findViewById(R.id.loadButton);

        // Cria ou abre o banco de dados SQLite
        database = openOrCreateDatabase("MeuBancoDeDados", MODE_PRIVATE, null);

        // Cria a tabela se não existir
        database.execSQL("CREATE TABLE IF NOT EXISTS Contatos (Nome TEXT, Numero TEXT, Endereco TEXT);");

        salvaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Salva os dados no banco de dados
                salvarDados();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Carrega os dados do banco de dados
                carregarDados();
            }
        });
    }

    private void salvarDados() {
        String nome = editTextNome.getText().toString().trim();
        String numero = editTextNumero.getText().toString().trim();
        String endereco = editTextEndereco.getText().toString().trim();

        // Verifica se os campos estão preenchidos
        if (nome.isEmpty() || numero.isEmpty() || endereco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insere os dados no banco de dados
        ContentValues values = new ContentValues();
        values.put("Nome", nome);
        values.put("Numero", numero);
        values.put("Endereco", endereco);

        long result = database.insert("Contatos", null, values);

        if (result != -1) {
            Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show();

            // Limpa os campos de texto após salvar os dados
            editTextNome.setText("");
            editTextNumero.setText("");
            editTextEndereco.setText("");
        } else {
            Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show();
        }
    }


    private int currentItemId = 0;

    private void carregarDados() {
        // Carrega os dados do banco de dados
        Cursor cursor = database.rawQuery("SELECT * FROM Contatos", null);

        if (cursor.moveToFirst()) {
            // Move para o item correspondente
            for (int i = 0; i < currentItemId; i++) {
                if (!cursor.moveToNext()) {
                    // Se não houver mais itens, reinicia para o primeiro
                    cursor.moveToFirst();
                }
            }

            // Exibe os dados
            @SuppressLint("Range") String nome = cursor.getString(cursor.getColumnIndex("Nome"));
            @SuppressLint("Range") String numero = cursor.getString(cursor.getColumnIndex("Numero"));
            @SuppressLint("Range") String endereco = cursor.getString(cursor.getColumnIndex("Endereco"));

            // Atualiza os campos de texto
            editTextNome.setText(nome);
            editTextNumero.setText(numero);
            editTextEndereco.setText(endereco);

            Toast.makeText(this, "Dados carregados com sucesso", Toast.LENGTH_SHORT).show();

            // Atualiza o contador para o próximo item na próxima chamada
            currentItemId++;

            if (currentItemId >= cursor.getCount()) {
                // Se atingir o final da lista, reinicia para o primeiro item
                currentItemId = 0;
            }
        } else {
            Toast.makeText(this, "Nenhum dado encontrado", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }


    @Override
    protected void onDestroy() {
        // Fecha o banco de dados ao destruir a atividade
        if (database != null && database.isOpen()) {
            database.close();
        }

        super.onDestroy();
    }
}
