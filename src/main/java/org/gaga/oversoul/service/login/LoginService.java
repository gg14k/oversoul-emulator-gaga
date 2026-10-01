package org.gaga.oversoul.service.login;

import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.player.character.PlayerCharacter;
import org.gaga.oversoul.player.data.AccountData;
import org.gaga.oversoul.player.data.PlayerProfileData;
import org.gaga.oversoul.repository.PlayerCharactersRepository;
import org.gaga.oversoul.repository.PlayerProfileRepository;
import org.gaga.oversoul.repository.PlayersRepository;
import org.gaga.oversoul.utils.GameLogger;
import org.jooq.exception.DataAccessException;

import java.util.List;

public final class LoginService {

    private final PlayersRepository playersRepository;

    private final PlayerProfileRepository profileRepository;

    private final PlayerCharactersRepository charactersRepository;

    public LoginService(
            PlayersRepository playersRepository,
            PlayerProfileRepository profileRepository,
            PlayerCharactersRepository charactersRepository
    ) {
        this.playersRepository = playersRepository;
        this.profileRepository = profileRepository;
        this.charactersRepository = charactersRepository;
    }

    public LoginResult login(
            PlayerEntity player,
            long playerId,
            String token
    ) {
        if (player.isLoaded()) {
            return LoginResult.failure(
                    LoginStatus.ALREADY_AUTHENTICATED
            );
        }

        if (token == null || token.isBlank()) {
            return LoginResult.failure(
                    LoginStatus.INVALID_CREDENTIALS
            );
        }

        try {
            AccountData account =
                    playersRepository
                            .findByIdAndToken(
                                    playerId,
                                    token
                            )
                            .orElse(null);

            if (account == null) {
                return LoginResult.failure(
                        LoginStatus.INVALID_CREDENTIALS
                );
            }

            PlayerProfileData profile =
                    profileRepository
                            .findByPlayerId(
                                    account.playerId()
                            )
                            .orElse(null);

            if (profile == null) {
                return LoginResult.failure(
                        LoginStatus.PROFILE_NOT_FOUND
                );
            }

            List<PlayerCharacter> characters =
                    charactersRepository.findByPlayerId(
                            account.playerId()
                    );

            if (characters.isEmpty()) {
                return LoginResult.failure(
                        LoginStatus.NO_CHARACTERS
                );
            }

            if (account.activePlayerCharacterId() == null) {
                return LoginResult.failure(
                        LoginStatus.ACTIVE_CHARACTER_NOT_FOUND
                );
            }

            boolean activeCharacterExists =
                    characters
                            .stream()
                            .anyMatch(character ->
                                    character.playerCharacterId()
                                            == account.activePlayerCharacterId()
                            );

            if (!activeCharacterExists) {
                return LoginResult.failure(
                        LoginStatus.ACTIVE_CHARACTER_NOT_FOUND
                );
            }

            player.initialize(
                    account,
                    profile,
                    characters
            );


            GameLogger.info(
                    "Player '{}' authenticated successfully.",
                    account.username()
            );

            return LoginResult.success();

        } catch (DataAccessException e) {
            GameLogger.error(
                    "Database error while authenticating player "
                            + playerId,
                    e
            );

            return LoginResult.failure(
                    LoginStatus.INTERNAL_ERROR
            );
        }
    }
}