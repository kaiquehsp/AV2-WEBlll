package com.autobots.automanager.controles;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelos.AdicionadorLinkEndereco;
import com.autobots.automanager.modelos.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/clientes/{clienteId}/endereco")
public class EnderecoControle {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoControle.class);

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private AdicionadorLinkEndereco adicionadorLink;

    @GetMapping
    public ResponseEntity<Endereco> obterEndereco(@PathVariable long clienteId) {
        logger.info("Buscando endereço do cliente ID: {}", clienteId);
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        
        if (clienteBusca.isEmpty() || clienteBusca.get().getEndereco() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Endereco endereco = clienteBusca.get().getEndereco();
        adicionadorLink.adicionarLink(endereco, clienteId);
        return new ResponseEntity<>(endereco, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Endereco> cadastrarEndereco(@PathVariable long clienteId, @RequestBody Endereco endereco) {
        logger.info("Cadastrando endereço para o cliente ID: {}", clienteId);
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Cliente cliente = clienteBusca.get();
        
        if (cliente.getEndereco() != null) {
            logger.warn("Cliente ID: {} já possui um endereço cadastrado.", clienteId);
            return new ResponseEntity<>(HttpStatus.CONFLICT); 
        }
        
        cliente.setEndereco(endereco);
        clienteRepositorio.save(cliente);
        
        adicionadorLink.adicionarLink(endereco, clienteId);
        return new ResponseEntity<>(endereco, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable long clienteId, @RequestBody Endereco atualizacao) {
        logger.info("Atualizando endereço do cliente ID: {}", clienteId);
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        
        if (clienteBusca.isEmpty() || clienteBusca.get().getEndereco() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Cliente cliente = clienteBusca.get();
        
        EnderecoAtualizador atualizador = new EnderecoAtualizador();
        atualizador.atualizar(cliente.getEndereco(), atualizacao);
        
        clienteRepositorio.save(cliente);
        adicionadorLink.adicionarLink(cliente.getEndereco(), clienteId);
        
        return new ResponseEntity<>(cliente.getEndereco(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> excluirEndereco(@PathVariable long clienteId) {
        logger.info("Excluindo endereço do cliente ID: {}", clienteId);
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        
        if (clienteBusca.isEmpty() || clienteBusca.get().getEndereco() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Cliente cliente = clienteBusca.get();
        cliente.setEndereco(null); 
        
        clienteRepositorio.save(cliente);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}