package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelos.AdicionadorLinkTelefone;
import com.autobots.automanager.modelos.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/clientes/{clienteId}/telefones")
public class TelefoneControle {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private AdicionadorLinkTelefone adicionadorLink;

    @GetMapping
    public ResponseEntity<List<Telefone>> obterTelefones(@PathVariable long clienteId) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Telefone> telefones = clienteBusca.get().getTelefones();
        telefones.forEach(t -> adicionadorLink.adicionarLink(t, clienteId));
        return new ResponseEntity<>(telefones, HttpStatus.OK);
    }

    @GetMapping("/{telefoneId}")
    public ResponseEntity<Telefone> obterTelefoneEspecifico(@PathVariable long clienteId, @PathVariable long telefoneId) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        
        Optional<Telefone> telefoneAlvo = clienteBusca.get().getTelefones().stream()
                .filter(t -> t.getId().equals(telefoneId))
                .findFirst();

        if (telefoneAlvo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        adicionadorLink.adicionarLink(telefoneAlvo.get(), clienteId);
        return new ResponseEntity<>(telefoneAlvo.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Telefone> cadastrarTelefone(@PathVariable long clienteId, @RequestBody Telefone telefone) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cliente cliente = clienteBusca.get();
        cliente.getTelefones().add(telefone);
        clienteRepositorio.save(cliente);
        adicionadorLink.adicionarLink(telefone, clienteId);
        return new ResponseEntity<>(telefone, HttpStatus.CREATED);
    }

    @PutMapping("/{telefoneId}")
    public ResponseEntity<Telefone> atualizarTelefone(@PathVariable long clienteId, @PathVariable long telefoneId, @RequestBody Telefone atualizacao) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cliente cliente = clienteBusca.get();
        Optional<Telefone> telefoneAlvo = cliente.getTelefones().stream()
                .filter(t -> t.getId().equals(telefoneId))
                .findFirst();

        if (telefoneAlvo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //a
        TelefoneAtualizador atualizador = new TelefoneAtualizador();
        atualizador.atualizar(telefoneAlvo.get(), atualizacao);
        clienteRepositorio.save(cliente);
        
        adicionadorLink.adicionarLink(telefoneAlvo.get(), clienteId); 
        return new ResponseEntity<>(telefoneAlvo.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{telefoneId}")
    public ResponseEntity<?> excluirTelefone(@PathVariable long clienteId, @PathVariable long telefoneId) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cliente cliente = clienteBusca.get();
        boolean removido = cliente.getTelefones().removeIf(t -> t.getId().equals(telefoneId));
        if (removido) {
            clienteRepositorio.save(cliente);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}