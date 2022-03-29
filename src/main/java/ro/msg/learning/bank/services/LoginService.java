package ro.msg.learning.bank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.msg.learning.bank.dtos.LoginDTO;

import ro.msg.learning.bank.dtos.OperationDTO;
import ro.msg.learning.bank.entities.Login;

import ro.msg.learning.bank.entities.Operation;
import ro.msg.learning.bank.exceptions.NotFoundException;
import ro.msg.learning.bank.repositories.LoginRepository;
import ro.msg.learning.bank.repositories.OperationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository loginRepository;
    private final OperationRepository operationRepository;

    public List<LoginDTO> listAll() {
        return loginRepository.findAll().stream()
                .map(LoginDTO::of)
                .collect(Collectors.toList());
    }

    public LoginDTO readById(int id) {
        return loginRepository.findById(id)
                .map(LoginDTO::of)
                .orElseThrow(NotFoundException::new);
    }

    public LoginDTO create(LoginDTO input) {
        Login login = input.toEntity();

        Operation operation = createOperation(input, LocalDateTime.now());

        login.setOperation(operation);
        return LoginDTO.of(loginRepository.save(login));
    }
    private Operation createOperation(LoginDTO input, LocalDateTime timestampOfOperation) {

        OperationDTO operationDTO = OperationDTO.of(input.getOperation());
        operationDTO.setTimeStamp(timestampOfOperation);
        operationRepository.save(operationDTO.toEntity());
        int sizeOfOperationList=operationRepository.findAll().size();
        return operationRepository.findAll().get(sizeOfOperationList-1);
    }

    public void deleteById(int id) {
        loginRepository.deleteById(id);
    }

    public void deleteAll() {
        loginRepository.deleteAll();
    }

    public LoginDTO updateById(int id, LoginDTO input) {
        Login login =
                loginRepository.findById(id).orElseThrow(NotFoundException::new);
        input.copyToEntity(login);
        loginRepository.save(login);
        return LoginDTO.of(login);
    }

}
