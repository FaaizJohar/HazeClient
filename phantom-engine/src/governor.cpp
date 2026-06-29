#include "phantom/governor.hpp"
#include <cstdlib>
#include <algorithm>

namespace phantom {

AdaptiveResourceGovernor::AdaptiveResourceGovernor(std::shared_ptr<HardwareOracle> oracle)
    : m_oracle(std::move(oracle)), 
      m_current_tier(MemoryTier::Feather), 
      m_worker_threads(2) {}

Result<void> AdaptiveResourceGovernor::initialize(const HardwareProfile& profile) noexcept {
    evaluate_tier(profile);

    // Register to listen to memory pressure events
    m_oracle->register_pressure_callback([this](bool pressure) {
        if (pressure && m_current_tier != MemoryTier::Ghost) {
            m_current_tier = MemoryTier::Feather;
            m_worker_threads = 2;
            // In a full implementation, we'd signal caches to evict
        }
    });

    return {};
}

void AdaptiveResourceGovernor::suspend() noexcept {
    m_current_tier = MemoryTier::Ghost;
    m_worker_threads = 0; // Suspend worker pools
}

void AdaptiveResourceGovernor::resume() noexcept {
    if (auto profile = m_oracle->get_current_profile()) {
        evaluate_tier(*profile);
    }
}

void* AdaptiveResourceGovernor::allocate_budgeted(size_t size) {
    // A production implementation would use a budgeted arena allocator
    return std::malloc(size);
}

void AdaptiveResourceGovernor::free_budgeted(void* ptr, size_t /*size*/) {
    std::free(ptr);
}

MemoryTier AdaptiveResourceGovernor::get_current_tier() const noexcept {
    return m_current_tier.load(std::memory_order_acquire);
}

size_t AdaptiveResourceGovernor::get_active_worker_threads() const noexcept {
    return m_worker_threads.load(std::memory_order_acquire);
}

void AdaptiveResourceGovernor::evaluate_tier(const HardwareProfile& profile) {
    if (profile.total_ram_mb >= 8192) {
        m_current_tier = MemoryTier::HighEnd;
        m_worker_threads = std::max<size_t>(4, profile.core_count);
    } else if (profile.total_ram_mb >= 4096) {
        m_current_tier = MemoryTier::MidRange;
        m_worker_threads = std::max<size_t>(2, profile.core_count / 2);
    } else {
        m_current_tier = MemoryTier::Feather;
        m_worker_threads = 2; // Cooperative thread pool limit
    }
}

} // namespace phantom
