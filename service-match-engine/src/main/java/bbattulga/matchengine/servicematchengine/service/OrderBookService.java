package bbattulga.matchengine.servicematchengine.service;

import bbattulga.matchengine.servicematchengine.dto.engine.OrderBookPriceLevel;
import lombok.RequiredArgsConstructor;
import org.jgrapht.util.AVLTree;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderBookService extends AVLTree<OrderBookPriceLevel> {

}
