package io.github.bindglam.nationwar;

import java.util.logging.Logger;

public record Context(
        Logger logger,
        NationWarPlugin plugin,
        NationWarConfig config
) {
}
