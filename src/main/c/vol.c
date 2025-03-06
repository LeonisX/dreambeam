#include <windows.h>
#include <stdio.h>

// Main code from https://gist.github.com/duketwo/74a933f35366ae1f56192e9973f88eff
int main(int argc, char *argv[])
{
    // The drive letter of the hard drive you want to get the serial number for
    const size_t cSize = strlen(argv[1])+1;
    wchar_t drive[] = L"A:\\";
    mbstowcs(drive, argv[1], cSize);

    // Buffer to receive the volume serial number
    DWORD serial_number;

    // Buffer to receive the volume label
    wchar_t volume_name[MAX_PATH + 1] = { 0 };

    // Size of the volume label buffer
    DWORD volume_name_size = MAX_PATH + 1;

    // File system flags
    DWORD file_system_flags;

    // File system name buffer
    wchar_t file_system_name[MAX_PATH + 1] = { 0 };

    // Size of the file system name buffer
    DWORD file_system_name_size = MAX_PATH + 1;

    // Get the volume information
    BOOL success = GetVolumeInformationW(
        drive,
        volume_name,
        volume_name_size,
        &serial_number,
        NULL,
        &file_system_flags,
        file_system_name,
        file_system_name_size
    );

    if (success)
    {
        wprintf(L"SERIAL: %d\n", serial_number);
        wprintf(L"LABEL: %s\n", volume_name);
        wprintf(L"SYSTEM: %s\n", file_system_name); // CDFS, NTFS, FAT32, exFAT
    }
    else
    {
        wprintf(L"GetVolumeInformation failed with error code %d\n", GetLastError());
    }

    return 0;
}

