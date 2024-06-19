package co.ostorlab.insecure_app.bugs.calls;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import co.ostorlab.insecure_app.BugRule;

public class SecurePathTraversal extends BugRule {
    public class Provider extends ContentProvider {

        private static final String BASE_DIRECTORY = Environment.getExternalStorageDirectory().getPath();

        @Override
        public boolean onCreate() {
            return false;
        }

        @Nullable
        @Override
        public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
            return null;
        }

        @Nullable
        @Override
        public String getType(@NonNull Uri uri) {
            return null;
        }

        @Nullable
        @Override
        public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
            return null;
        }

        @Override
        public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
            return 0;
        }

        @Override
        public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
            return 0;
        }

        @Override
        public ParcelFileDescriptor openFile(Uri uri, @NonNull String mode) throws FileNotFoundException {
            try {
                // Insecure pattern are from the methods getPath, getAbsolutePath, toPath, toAbsolutePath.
                File file = new File(BASE_DIRECTORY, uri.getLastPathSegment()).getCanonicalFile();
                if (!file.getPath().startsWith(BASE_DIRECTORY)) {
                    throw new SecurityException("Attempt to access a file outside the base directory.");
                }
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            } catch (IOException e) {
                throw new FileNotFoundException("File not found or inaccessible.");
            }
        }
    }

    @Override
    public void run(String user_input) throws Exception {
        if (!user_input.isEmpty()) {
            Provider taint_provider = new Provider();
            Uri.Builder taint_builder = new Uri.Builder();
            taint_builder.scheme("https");
            taint_builder.authority(user_input);
            Uri uri = taint_builder.build();
            taint_provider.openFile(uri, "not used parameter");
        }

        Provider provider = new Provider();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("ostorlab.co");
        Uri uri = builder.build();

        provider.openFile(uri, "not used parameter");
    }

    @Override
    public String getDescription() {
        return "Secure call to getLastPathSegment with Uri parameter";
    }
}
