package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.ClientDTO;
import ro.msg.learning.bank.entities.Client;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public List<ClientDTO> listAll() {
        return clientRepository.findAll().stream()
                .map(ClientDTO::of)
                .collect(Collectors.toList());
    }

    public ClientDTO readByFirstNameAndLastName(String firstName, String lastName) {
        return clientRepository.findByFirstnameAndLastname(firstName, lastName)
                .map(ClientDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public ClientDTO create(ClientDTO input) {
        Client client = input.toEntity();
        return ClientDTO.of(clientRepository.save(client));
    }

    public void deleteByFirstnameAndLastname(String firstname, String lastname) {
        clientRepository.deleteByFirstnameAndLastname(firstname, lastname);
    }

    public void deleteAll() {
        clientRepository.deleteAll();
    }

    public ClientDTO updateByFirstNameAndLastName(String firstName, String lastName, ClientDTO input) {
        Client client =
                clientRepository.findByFirstnameAndLastname(firstName, lastName).orElseThrow(NotFoundException::new);
        input.copyToEntity(client);
        clientRepository.save(client);
        return ClientDTO.of(client);
    }
}
