/*
 * MIT License
 *
 * Copyright (c) today.year Elias Nogueira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.eliasnogueira.credit.controller;

import com.eliasnogueira.credit.dto.SimulationDto;
import com.eliasnogueira.credit.entity.Simulation;
import com.eliasnogueira.credit.exception.SimulationByNameNotFoundException;
import com.eliasnogueira.credit.exception.SimulationException;
import com.eliasnogueira.credit.repository.SimulationRepository;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.text.MessageFormat.format;

@RestController
@RequestMapping("/api/v1/simulations")
public class SimulationsController {

    private final SimulationRepository repository;
    private static final String CPF_NOT_FOUND = "CPF {0} not found";

    public SimulationsController(SimulationRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    @SneakyThrows
    List<Simulation> getSimulation(@RequestParam(name = "name", required = false) String name) {
        Example<Simulation> example =
                Example.of(Simulation.builder().name(name).build(),
                        ExampleMatcher.matchingAny().
                                withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()));

        List<Simulation> simulationsFound = repository.findAll(example);

        if (simulationsFound.isEmpty()) throw new SimulationByNameNotFoundException();

        return simulationsFound;
    }

    @GetMapping("/{cpf}")
    ResponseEntity<Simulation> one(@PathVariable String cpf) {
        return repository.findByCpf(cpf).
                map(simulation -> ResponseEntity.ok().body(simulation)).
                orElseThrow(() -> new SimulationException(format(CPF_NOT_FOUND, cpf)));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> newSimulation(@Valid @RequestBody SimulationDto simulation) {
        Simulation createdSimulation = repository.save(new ModelMapper().map(simulation, Simulation.class));
        URI location = ServletUriComponentsBuilder.
                fromCurrentRequest().
                path("/{cpf}").
                buildAndExpand(createdSimulation.getCpf()).
                toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{cpf}")
    Simulation updateSimulation(@RequestBody SimulationDto simulation, @PathVariable String cpf) {
        return update(new ModelMapper().
                map(simulation, Simulation.class), cpf).
                orElseThrow(() -> new SimulationException(format(CPF_NOT_FOUND, cpf)));
    }

    @DeleteMapping("/{cpf}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String cpf) {
        if (repository.findByCpf(cpf).isEmpty()) throw new SimulationException(format(CPF_NOT_FOUND, cpf));

        repository.deleteByCpf(cpf);
    }

    private Optional<Simulation> update(Simulation newSimulation, String cpf) {
        return repository.findByCpf(cpf).map(simulation -> {
            setIfNotNull(simulation::setId, newSimulation.getId());
            setIfNotNull(simulation::setName, newSimulation.getName());
            setIfNotNull(simulation::setCpf, newSimulation.getCpf());
            setIfNotNull(simulation::setEmail, newSimulation.getEmail());
            setIfNotNull(simulation::setInstallments, newSimulation.getInstallments());
            setIfNotNull(simulation::setAmount, newSimulation.getAmount());

            return repository.save(simulation);
        });
    }

    private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
        if (value != null) setter.accept(value);
    }
}
