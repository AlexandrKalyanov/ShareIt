package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutcomeDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestOutcomeDto create(long userId, ItemRequestIncomeDto itemRequestIncomeDto) {
        User user = userRepository.getUserOrException(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description(itemRequestIncomeDto.getDescription())
                .build();
        return ItemRequestMapper.toItemRequestOutcomeDto(requestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestOutcomeDto> findAllByUserId(long userId) {
        userRepository.getUserOrException(userId);

        List<ItemRequest> allByRequesterId = requestRepository.findAllByRequester_Id(userId);

        List<Long> requestsIds = allByRequesterId
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.searchByRequestIds(requestsIds);
        if (items.isEmpty()) {
            List<ItemRequestOutcomeDto> list = allByRequesterId
                    .stream()
                    .map(ItemRequestMapper::toItemRequestOutcomeDto)
                    .collect(Collectors.toList());
            for (ItemRequestOutcomeDto itemRequestOutcomeDto : list) {
                itemRequestOutcomeDto.setItems(Collections.emptyList());
            }
            return list;
        }
        List<ItemForRequest> itemResponseDtos = items
                .stream()
                .filter(item -> item.getRequest() != null)
                .map(ItemMapper::toItemForRequest)
                .collect(Collectors.toList());

        List<ItemRequestOutcomeDto> itemRequestOutcomeDtos = allByRequesterId
                .stream()
                .map(ItemRequestMapper::toItemRequestOutcomeDto)
                .collect(Collectors.toList());
        for (ItemRequestOutcomeDto itemRequestOutcomeDto : itemRequestOutcomeDtos) {
            addItemsToRequest(itemResponseDtos, itemRequestOutcomeDto);
        }
        return itemRequestOutcomeDtos;
    }

    @Override
    public List<ItemRequestOutcomeDto> findAll(long userId, int from, int size) {
        userRepository.getUserOrException(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequestOutcomeDto> itemRequestOutcomeDtos = requestRepository.findAllWithPage(userId, pageRequest)
                .stream()
                .map(ItemRequestMapper::toItemRequestOutcomeDto)
                .collect(Collectors.toList());
        List<Long> itemRequestIds = itemRequestOutcomeDtos
                .stream()
                .map(ItemRequestOutcomeDto::getId)
                .collect(Collectors.toList());
        List<Item> itemList = itemRepository.searchByRequestIds(itemRequestIds);
        if (itemList == null) {
            itemRequestOutcomeDtos.forEach(itemRequestOutcomeDto -> itemRequestOutcomeDto.setItems(Collections.emptyList()));
            return itemRequestOutcomeDtos;
        }
        List<ItemForRequest> itemResponseDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemResponseDtos.add(ItemRequestMapper.toItemForRequest(item));
        }
        for (ItemRequestOutcomeDto itemRequestOutcomeDto : itemRequestOutcomeDtos) {
            Collection<ItemForRequest> itemForRequestsList = new ArrayList<>();
            for (ItemForRequest itemForRequest : itemResponseDtos) {
                if (itemForRequest.getRequestId() != null && itemForRequest.getRequestId().equals(itemRequestOutcomeDto.getId())) {
                    itemForRequestsList.add(itemForRequest);
                }
                itemRequestOutcomeDto.setItems(itemForRequestsList);
            }
        }
        return itemRequestOutcomeDtos;
    }

    @Override
    public ItemRequestOutcomeDto findRequest(long userId, long requestId) {
        userRepository.getUserOrException(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new ObjectNotFoundException("request not found"));
        List<Item> items = itemRepository.searchByRequestIds(List.of(itemRequest.getId()));
        if (items.isEmpty()) {
            ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestMapper.toItemRequestOutcomeDto(itemRequest);
            itemRequestOutcomeDto.setItems(Collections.emptyList());
            return itemRequestOutcomeDto;
        }
        List<ItemForRequest> itemResponseDtos = items
                .stream()
                .filter(item -> item.getRequest() != null)
                .map(ItemMapper::toItemForRequest)
                .collect(Collectors.toList());
        ItemRequestOutcomeDto itemRequestOutcomeDto = ItemRequestMapper.toItemRequestOutcomeDto(itemRequest);
        addItemsToRequest(itemResponseDtos, itemRequestOutcomeDto);
        return itemRequestOutcomeDto;
    }

    private void addItemsToRequest(List<ItemForRequest> itemResponseDtos, ItemRequestOutcomeDto itemRequestOutcomeDto) {
        Collection<ItemForRequest> itemResponseList = new ArrayList<>();
        for (ItemForRequest itemResponseDto : itemResponseDtos) {
            if (itemResponseDto.getRequestId() != null && itemResponseDto.getRequestId().equals(itemRequestOutcomeDto.getId())) {
                itemResponseList.add(itemResponseDto);
            }
        }
        itemRequestOutcomeDto.setItems(itemResponseList);
    }
}
