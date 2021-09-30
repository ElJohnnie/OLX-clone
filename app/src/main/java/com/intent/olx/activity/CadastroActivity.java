package com.intent.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.intent.olx.R;
import com.intent.olx.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;

    private FirebaseAuth autenticaco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //inicializando os componentes de login
        inicializarComponentes();
        autenticaco = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //ao clicar em acessar, vai existir as verificações
        botaoAcessar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if( !email.isEmpty()){
                    if( !senha.isEmpty()){
                        //verificar o estado do switch
                        if( tipoAcesso.isChecked() ){
                            //cadastro
                            autenticaco.createUserWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(
                                                CadastroActivity.this,
                                                "Cadastro realizado com sucesso.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        //Direcionar para a tela principal do App
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));

                                    }else{
                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite uma senha mais forte";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Por favor, digite um E-mail válido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Está conta já foi cadastrada";
                                        }catch (Exception e){
                                            erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(
                                                CadastroActivity.this,
                                                "Erro: " + erroExcecao,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }else{
                            //login
                            autenticaco.signInWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(
                                                CadastroActivity.this,
                                                "Logado com sucesso.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        //direcionar para a tela de anuncios
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                    }else{
                                        Toast.makeText(
                                                CadastroActivity.this,
                                                "Erro ao fazer login." + task.getException(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }
                    }else{
                        Toast.makeText(
                                CadastroActivity.this,
                                "Preencha a senha.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }else{
                    Toast.makeText(
                            CadastroActivity.this,
                            "Preencha o E-Mail.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);
        botaoAcessar = findViewById(R.id.buttonAcesso);
    }
}