package com.example.sisgesc.Repository;

// Como o Repository devolve o resultado para a tela quando a internet responde.
// Os dois métodos rodam na thread principal, então a tela já pode atualizar as views neles.
public interface RepositoryCallback<T> {
    void onSucesso(T resultado);
    void onErro(String mensagem);
}
