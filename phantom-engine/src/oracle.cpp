#include "phantom/oracle.hpp"

#if defined(_WIN32)
#include <windows.h>
#elif defined(__linux__)
#include <unistd.h>
#include <sys/sysinfo.h>
#endif

namespace phantom {

HardwareOracle::HardwareOracle() : m_current_profile{} {}

Result<void> HardwareOracle::initialize(const HardwareProfile& profile) noexcept {
    // Usually we would override profile if doing full hardware detection
    m_current_profile = profile;
    detect_cpu_topology();
    detect_ram_capacity();
    detect_storage_medium();
    
    return {};
}

void HardwareOracle::suspend() noexcept {
    // Release dynamic tracking buffers
}

void HardwareOracle::resume() noexcept {
    // Restart dynamic tracking
}

Result<HardwareProfile> HardwareOracle::get_current_profile() const noexcept {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_current_profile;
}

void HardwareOracle::register_pressure_callback(std::function<void(bool)> cb) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_pressure_callbacks.push_back(std::move(cb));
}

void HardwareOracle::detect_cpu_topology() {
#if defined(_WIN32)
    SYSTEM_INFO sysinfo;
    GetSystemInfo(&sysinfo);
    m_current_profile.core_count = static_cast<uint8_t>(sysinfo.dwNumberOfProcessors);
#elif defined(__linux__)
    long num_cores = sysconf(_SC_NPROCESSORS_ONLN);
    m_current_profile.core_count = static_cast<uint8_t>(num_cores);
#else
    m_current_profile.core_count = 2; // Safe Feather fallback
#endif
}

void HardwareOracle::detect_ram_capacity() {
#if defined(_WIN32)
    MEMORYSTATUSEX status;
    status.dwLength = sizeof(status);
    if (GlobalMemoryStatusEx(&status)) {
        m_current_profile.total_ram_mb = status.ullTotalPhys / (1024 * 1024);
    }
#elif defined(__linux__)
    struct sysinfo info;
    if (sysinfo(&info) == 0) {
        m_current_profile.total_ram_mb = (info.totalram * info.mem_unit) / (1024 * 1024);
    }
#endif
}

void HardwareOracle::detect_storage_medium() {
    // A simplified heuristic for Feather Mode detection.
    // In production, we query FSCTL_GET_RETRIEVAL_POINTERS or /sys/block/sda/queue/rotational
    m_current_profile.is_hdd = false;
}

} // namespace phantom
