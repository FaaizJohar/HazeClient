#pragma once

#include "phantom/module.hpp"
#include "phantom/oracle.hpp"
#include <string>

namespace phantom {

struct InstanceId {
    std::string uuid;
};

// Represents a launched child process
class ProcessHandle {
public:
    ~ProcessHandle() = default;
    void terminate();
};

class PhantomLaunchEngine : public IModule {
public:
    explicit PhantomLaunchEngine(std::shared_ptr<HardwareOracle> oracle);
    ~PhantomLaunchEngine() override = default;

    Result<void> initialize(const HardwareProfile& profile) noexcept override;
    void suspend() noexcept override;
    void resume() noexcept override;

    // Launch an instance using generated JVM profiles
    Result<ProcessHandle> launch_instance(const InstanceId& id) noexcept;
    Result<void> abort_launch() noexcept;

private:
    std::shared_ptr<HardwareOracle> m_oracle;

    std::string generate_jvm_flags(const HardwareProfile& profile) const;
};

} // namespace phantom
