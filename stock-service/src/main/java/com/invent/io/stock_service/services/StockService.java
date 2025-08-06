package com.invent.io.stock_service.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.invent.io.stock_service.enums.MovementType;
import com.invent.io.stock_service.model.Stock;
import com.invent.io.stock_service.model.StockMovement;
import com.invent.io.stock_service.repository.StockMovementRepository;
import com.invent.io.stock_service.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    //* Adiciona estoque para um produto específico
    public Stock addStock(String skuCode, int quantity) {
        Stock stock = stockRepository.findBySkuCode(skuCode)
                .orElseGet(() -> new Stock(null, skuCode, 0, 0, 0));

        stock.setQuantityTotal(stock.getQuantityTotal() + quantity);
        stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);

        Stock stockSaved = stockRepository.save(stock);
        recordMovement(skuCode, MovementType.IN, quantity, "Entrada de estoque");

        return stockSaved;
    }

    //* Remove estoque para um produto específico
    public Stock decrementStock(String skuCode, int quantity) {
        Stock stock = getStockOrThrow(skuCode);
        validateQuantity(stock.getQuantityAvailable(), quantity, skuCode, "disponível");

        stock.setQuantityAvailable(stock.getQuantityAvailable() - quantity);
        stock.setQuantityTotal(stock.getQuantityTotal() - quantity);

        Stock newStock = stockRepository.save(stock);
        recordMovement(skuCode, MovementType.OUT, quantity, "Saída de estoque");

        return newStock;
    }

    //* Valida se existe estoque disponível para um produto específico
    public boolean isInStock(String skuCode, int quantity) {
        return stockRepository.existsBySkuCodeAndQuantityAvailableIsGreaterThanEqual(skuCode, quantity);
    }

    //* Reserva estoque para um produto específico
    public Stock reserveStock(String skuCode, int quantity) {
        Stock stock = getStockOrThrow(skuCode);
        validateQuantity(stock.getQuantityAvailable(), quantity, skuCode, "disponível");

        stock.setQuantityAvailable(stock.getQuantityAvailable() - quantity);
        stock.setQuantityReserved(stock.getQuantityReserved() + quantity);

        Stock newStock = stockRepository.save(stock);
        recordMovement(skuCode, MovementType.RESERVED, quantity, "Reserva de estoque");

        return newStock;
    }

    //* Cancela a reserva do estoque para um produto específico
    public Stock releaseStock(String skuCode, int quantity) {
        Stock stock = getStockOrThrow(skuCode);
        validateQuantity(stock.getQuantityReserved(), quantity, skuCode, "reservada");

        stock.setQuantityReserved(stock.getQuantityReserved() - quantity);
        stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);

        Stock newStock = stockRepository.save(stock);
        recordMovement(skuCode, MovementType.CANCELED, quantity, "Liberação de estoque");

        return newStock;
    }

    //* Valida se existe produto com aquele skuCode
    private Stock getStockOrThrow(String skuCode) {
        return stockRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para o SKU: " + skuCode));
    }

    //* Valida a quantidade disponível para uma operação de estoque
    private void validateQuantity(int available, int required, String skuCode, String tipo) {
        if (available < required) {
            throw new IllegalArgumentException("Quantidade " + tipo + " insuficiente para SKU: " + skuCode);
        }
    }

    //* Salva a movimentação de estoque para histórico e auditoria
    private void recordMovement(String skuCode, MovementType type, int quantity, String description) {
        StockMovement movement = new StockMovement();
        movement.setSkuCode(skuCode);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setDescription(description);
        movement.setTimestamp(LocalDateTime.now());

        stockMovementRepository.save(movement);
    }

}