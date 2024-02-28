package server;

import service.GameList;

import java.util.Collection;

public record ListArray(Collection<GameList> games) {}
