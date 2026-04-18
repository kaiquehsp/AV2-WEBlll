package com.autobots.automanager.modelos;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Cliente;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<Cliente> {

    @Autowired
    private AdicionadorLinkDocumento adicionadorLinkDocumento;
    
    @Autowired
    private AdicionadorLinkTelefone adicionadorLinkTelefone;

    @Autowired
    private AdicionadorLinkEndereco adicionadorLinkEndereco;

    @Override
    public void adicionarLink(List<Cliente> lista) {
        for (Cliente cliente : lista) {
            adicionarLink(cliente);
        }
    }

    @Override
    public void adicionarLink(Cliente cliente) {
        long id = cliente.getId();
        
       
        cliente.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterCliente(id))
                .withSelfRel());


        cliente.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(DocumentoControle.class).obterDocumentos(id))
                .withRel("documentos_cliente"));

        cliente.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(TelefoneControle.class).obterTelefones(id))
                .withRel("telefones_cliente"));

        // 2. Itera sobre os itens internos para popular os links deles
        if (cliente.getDocumentos() != null) {
            cliente.getDocumentos().forEach(doc -> adicionadorLinkDocumento.adicionarLink(doc, id));
        }
        
        if (cliente.getTelefones() != null) {
            cliente.getTelefones().forEach(tel -> adicionadorLinkTelefone.adicionarLink(tel, id));
        }

        if (cliente.getEndereco() != null) {
            adicionadorLinkEndereco.adicionarLink(cliente.getEndereco(), id);
        }
    }
}