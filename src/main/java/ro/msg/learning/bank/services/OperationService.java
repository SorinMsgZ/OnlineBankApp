package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.entities.OperationType;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.AppUserRepository;
import ro.msg.learning.bank.repositories.OperationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OperationService {


    private final OperationRepository operationRepository;
    private final AppUserRepository appUserRepository;
    private final LoginService loginService;

    public List<OperationDTO> listAll() {
        return operationRepository.findAll().stream()
                .map(OperationDTO::of)
                .collect(Collectors.toList());
    }

    public List<OperationDTO> listAllLoginOperations() {
        return operationRepository.findByType(OperationType.LOGIN).stream()
                .map(OperationDTO::of)
                .collect(Collectors.toList());
    }

    public OperationDTO readById(int id) {
        return operationRepository.findById(id)
                .map(OperationDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public OperationDTO create(OperationDTO input) {
        Operation operation = input.toEntity();
        operation.setUserDetail(appUserRepository.findByUsername(input.getUsername()).orElseThrow(NotFoundException::new));
        return OperationDTO.of(operationRepository.save(operation));
    }

    public void deleteById(int id) {
        operationRepository.deleteById(id);
    }

    public void deleteAll() {
        operationRepository.deleteAll();
    }

    public OperationDTO updateById(int id, OperationDTO input) {
        Operation operation =
                operationRepository.findById(id).orElseThrow(NotFoundException::new);
        input.copyToEntity(operation);
        operationRepository.save(operation);
        return OperationDTO.of(operation);
    }

    public static boolean isExpectedOperation(OperationType expectedOperation, Operation actualOperation) {
        return actualOperation.getType().equals(expectedOperation);
    }
}
