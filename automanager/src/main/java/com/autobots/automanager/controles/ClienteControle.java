package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.modelos.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/clientes") 
public class ClienteControle {

    
    private static final Logger logger = LoggerFactory.getLogger(ClienteControle.class);

    @Autowired
    private ClienteRepositorio repositorio;
    
    @Autowired
    private AdicionadorLinkCliente adicionadorLink;

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable long id) {
        logger.info("Buscando cliente por ID: {}", id);
        
        
        Optional<Cliente> clienteBusca = repositorio.findById(id);
        
        if (clienteBusca.isEmpty()) {
            logger.warn("Cliente ID {} não encontrado.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
        
        Cliente cliente = clienteBusca.get();
        adicionadorLink.adicionarLink(cliente); 
        
        
        return new ResponseEntity<>(cliente, HttpStatus.OK); 
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> obterClientes() {
        logger.info("Listando todos os clientes");
        List<Cliente> clientes = repositorio.findAll();
        
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        } 
        
        adicionadorLink.adicionarLink(clientes);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
        logger.info("Iniciando cadastro de novo cliente");
        
        
        if (cliente.getId() != null) {
            logger.error("Tentativa de criar cliente passando ID existente.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Cliente clienteSalvo = repositorio.save(cliente);
        adicionadorLink.adicionarLink(clienteSalvo);
        
        
        return new ResponseEntity<>(clienteSalvo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable long id, @RequestBody Cliente atualizacao) {
        logger.info("Atualizando dados do cliente ID: {}", id);
        Optional<Cliente> clienteBusca = repositorio.findById(id);
        
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //a
        Cliente cliente = clienteBusca.get();
        ClienteAtualizador atualizador = new ClienteAtualizador();
        atualizador.atualizar(cliente, atualizacao);
        
        Cliente clienteAtualizado = repositorio.save(cliente);
        adicionadorLink.adicionarLink(clienteAtualizado);
        
        return new ResponseEntity<>(clienteAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(@PathVariable long id) {
        logger.info("Excluindo cliente ID: {}", id);
        Optional<Cliente> clienteBusca = repositorio.findById(id);
        
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        repositorio.delete(clienteBusca.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
}