package com.autobots.automanager.modelos;

import java.util.List;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionadorLinkTelefone implements AdicionadorLink<Telefone> {
    
    @Override
    public void adicionarLink(List<Telefone> lista) {
    }

    @Override
    public void adicionarLink(Telefone objeto) {
    }

    public void adicionarLink(Telefone objeto, long clienteId) {

        Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(TelefoneControle.class)
                .obterTelefoneEspecifico(clienteId, objeto.getId())) 
                .withSelfRel(); 
        
        objeto.add(linkProprio);

        Link linkColecao = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(TelefoneControle.class)
                .obterTelefones(clienteId))
                .withRel("telefones_do_cliente");
        
        objeto.add(linkColecao);
    }
}