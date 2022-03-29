package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.BankDTO;

import ro.msg.learning.bank.entities.Bank;

import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.BankRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BankService {
    private final BankRepository bankRepository;

    public List<BankDTO> listAll() {
        return bankRepository.findAll().stream()
                .map(BankDTO::of)
                .collect(Collectors.toList());
    }

    public BankDTO readById(int id) {
        return bankRepository.findById(id)
                .map(BankDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public BankDTO create(BankDTO input) {
        Bank bank = input.toEntity();
        bank.setName(input.getName());
        return BankDTO.of(bankRepository.save(bank));
    }

    public void deleteById(int id) {
        bankRepository.deleteById(id);
    }

    public void deleteAll() {
        bankRepository.deleteAll();
    }

    public BankDTO updateById(int id, BankDTO input) {
        Bank bank =
                bankRepository.findById(id).orElseThrow(NotFoundException::new);
        input.copyToEntity(bank);
        bankRepository.save(bank);
        return BankDTO.of(bank);
    }

}

