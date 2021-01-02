package com.app.RemoteDataSource;

import androidx.annotation.Nullable;

import java.io.IOException;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Common class used by API responses.
 *
 * @param <T> The {@code Response} object
 */
public class ApiResponse<T> {

    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;
    public final int code;

    /**
     * Default constructor for Internal Server Error (500) {@code http} code, {@code null} body
     * and {@code exception} message.
     *
     * @param error the Exception
     */
    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    /**
     * Handles the error response properly.
     *
     * @param response the Response from Api
     */
    public ApiResponse(Response<T> response) {
        code = response.code();
        // Response is successful
        if (response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
            // Response is not successful
        } else {
            String message = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException exception) {
                    Timber.e(exception, "Error while parsing error response");
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            body = null;
            errorMessage = message;
        }

    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
}
