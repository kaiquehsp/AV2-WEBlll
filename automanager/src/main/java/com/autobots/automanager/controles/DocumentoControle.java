package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelos.AdicionadorLinkDocumento;
import com.autobots.automanager.modelos.DocumentoAtualizador; 
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/clientes/{clienteId}/documentos")
public class DocumentoControle {
    
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    
    @Autowired
    private AdicionadorLinkDocumento adicionadorLink;

    @GetMapping
    public ResponseEntity<List<Documento>> obterDocumentos(@PathVariable long clienteId) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Documento> documentos = clienteBusca.get().getDocumentos();
        documentos.forEach(d -> adicionadorLink.adicionarLink(d, clienteId));
        return new ResponseEntity<>(documentos, HttpStatus.OK);
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<Documento> obterDocumentoEspecifico(@PathVariable long clienteId, @PathVariable long documentoId) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Documento> documento = clienteBusca.get().getDocumentos().stream()
                .filter(d -> d.getId().equals(documentoId))
                .findFirst();

        if (documento.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        adicionadorLink.adicionarLink(documento.get(), clienteId);
        return new ResponseEntity<>(documento.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Documento> cadastrarDocumento(@PathVariable long clienteId, @RequestBody Documento documento) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cliente cliente = clienteBusca.get();
        cliente.getDocumentos().add(documento);
        clienteRepositorio.save(cliente);
        
        adicionadorLink.adicionarLink(documento, clienteId);
        return new ResponseEntity<>(documento, HttpStatus.CREATED);
    }

    @PutMapping("/{documentoId}")
    public ResponseEntity<Documento> atualizarDocumento(@PathVariable long clienteId, @PathVariable long documentoId, @RequestBody Documento atualizacao) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cliente cliente = clienteBusca.get();
        Optional<Documento> documentoAlvo = cliente.getDocumentos().stream()
                .filter(d -> d.getId().equals(documentoId))
                .findFirst();

        if (documentoAlvo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DocumentoAtualizador atualizador = new DocumentoAtualizador();
        atualizador.atualizar(documentoAlvo.get(), atualizacao);

        clienteRepositorio.save(cliente);
        adicionadorLink.adicionarLink(documentoAlvo.get(), clienteId);
        
        return new ResponseEntity<>(documentoAlvo.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{documentoId}")
    public ResponseEntity<?> excluirDocumento(@PathVariable long clienteId, @PathVariable long documentoId) {
        Optional<Cliente> clienteBusca = clienteRepositorio.findById(clienteId);
        if (clienteBusca.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cliente cliente = clienteBusca.get();
        boolean removido = cliente.getDocumentos().removeIf(d -> d.getId().equals(documentoId));
        if (removido) {
            clienteRepositorio.save(cliente);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}