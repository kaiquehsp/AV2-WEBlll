package com.autobots.automanager.modelos;

import java.util.List;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.entidades.Endereco;

@Component
public class AdicionadorLinkEndereco implements AdicionadorLink<Endereco> {
    @Override
    public void adicionarLink(List<Endereco> lista) {}
    @Override
    public void adicionarLink(Endereco objeto) {}
    public void adicionarLink(Endereco objeto, long clienteId) {
        Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(EnderecoControle.class).obterEndereco(clienteId))
                .withSelfRel();
        objeto.add(linkProprio);
    }
}